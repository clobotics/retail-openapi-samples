package util

import (
	"bytes"
	"context"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
	"sort"
	"strconv"
	"strings"
	"time"
)

const (
	HttpMethodGet    HttpMethod = "GET"
	HttpMethodPost   HttpMethod = "POST"
	HttpMethodPut    HttpMethod = "PUT"
	HttpMethodDelete HttpMethod = "DELETE"
)

type BaseResponse struct {
	Code    int                    `json:"code"`
	Message string                 `json:"message"`
	Data    map[string]interface{} `json:"data"`
}

func DoRequest(req *http.Request) (map[string]interface{}, error) {
	// Set the timeout
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()
	req = req.WithContext(ctx)
	// Create the HTTP client
	client := &http.Client{}
	// Perform the HTTP request
	resp, err := client.Do(req)
	if err != nil {
		return nil, fmt.Errorf("error sending request :%s", err.Error())
	}
	if resp.StatusCode != 200 {
		return nil, fmt.Errorf("error response status:%v", resp.Status)
	}
	defer resp.Body.Close()
	// Read the response
	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return nil, fmt.Errorf("error reading response:%s", err.Error())
	}
	baseResponse := &BaseResponse{}
	if err := json.Unmarshal(body, baseResponse); err != nil {
		return nil, fmt.Errorf("error unmarshalling response:%s", err.Error())
	}
	if baseResponse.Code != 0 {
		return nil, fmt.Errorf("error request code:%v message:%s", baseResponse.Code, baseResponse.Message)
	}
	return baseResponse.Data, nil
}

func CreateRequest(appId, appSecret, host string, method HttpMethod, urlPath string, queryParams map[string]string, requestBody []byte) (*http.Request, error) {
	timestamp := time.Now().Unix()
	nonce := GenRandomString(10)
	// Calculate the Signature
	signature := ""
	switch method {
	case HttpMethodPut, HttpMethodPost:
		signature = GenSignature(appSecret, method, urlPath, nonce, timestamp, queryParams, requestBody, true)
	case HttpMethodGet, HttpMethodDelete:
		signature = GenSignature(appSecret, method, urlPath, nonce, timestamp, queryParams, requestBody, false)
	}
	// Create the Authorization header
	authorizationHeader := fmt.Sprintf("cbs:%s:%s", appId, signature)
	req, err := http.NewRequest(string(method), genUrl(host, urlPath, queryParams), bytes.NewBuffer(requestBody))
	if err != nil {
		return nil, err
	}
	// Add the required headers
	req.Header.Add("Content-Type", "application/json")
	req.Header.Add(HttpHeaderAuthorization, authorizationHeader)
	req.Header.Add(HttpHeaderTimestamp, strconv.FormatInt(timestamp, 10))
	req.Header.Add(HttpHeaderNonce, nonce)
	return req, nil
}

func CreateUploadRequest(appId, appSecret, host string, method HttpMethod, urlPath string, body *bytes.Buffer) (*http.Request, error) {
	timestamp := time.Now().Unix()
	nonce := GenRandomString(10)
	// Calculate the Signature
	signature := GenSignature(appSecret, method, urlPath, nonce, timestamp, map[string]string{}, []byte{}, false)
	// Create the Authorization header
	authorizationHeader := fmt.Sprintf("cbs:%s:%s", appId, signature)
	req, err := http.NewRequest(string(method), genUrl(host, urlPath, map[string]string{}), body)
	if err != nil {
		return nil, err
	}
	// Add the required headers
	// req.Header.Add("Content-Type", "multipart/form-data")
	req.Header.Add(HttpHeaderAuthorization, authorizationHeader)
	req.Header.Add(HttpHeaderTimestamp, strconv.FormatInt(timestamp, 10))
	req.Header.Add(HttpHeaderNonce, nonce)
	return req, nil
}

func genUrl(host, urlPath string, queryParams map[string]string) string {
	var keys []string
	for key := range queryParams {
		keys = append(keys, key)
	}
	sort.Strings(keys)
	var paramsItems []string
	for _, key := range keys {
		value := queryParams[key]
		paramsItems = append(paramsItems, fmt.Sprintf("%s=%s", key, value))
	}
	if len(paramsItems) > 0 {
		urlPath = urlPath + "?" + strings.Join(paramsItems, "&")
	}
	return host + urlPath
}
