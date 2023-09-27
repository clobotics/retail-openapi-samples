package util

import (
	"crypto/hmac"
	"crypto/md5"
	"crypto/sha256"
	"encoding/base64"
	"fmt"
	"sort"
	"strings"
)

type HttpMethod string

const (
	HttpHeaderAuthorization = "Authorization"
	HttpHeaderTimestamp     = "Timestamp"
	HttpHeaderNonce         = "Nonce"
)

// GenSignature generates the signature for the request
func GenSignature(appSecret string, method HttpMethod, urlPath, nonce string, timestamp int64, queryParams map[string]string, requestBody []byte, needContentMd5 bool) string {
	var contentMD5 = calculateContentMD5(requestBody)
	var keys []string
	for key := range queryParams {
		keys = append(keys, key)
	}
	// Sort the keys
	sort.Strings(keys)
	var canonicalizedResourceItems []string
	for _, key := range keys {
		value := queryParams[key]
		canonicalizedResourceItems = append(canonicalizedResourceItems, fmt.Sprintf("%s=%s", key, value))
	}
	canonicalizedResource := ""
	if len(canonicalizedResourceItems) > 0 {
		canonicalizedResource = urlPath + "?" + strings.Join(canonicalizedResourceItems, "&")
	} else {
		canonicalizedResource = urlPath
	}
	// Construct StringToSign
	stringToSign := ""
	if needContentMd5 {
		stringToSign = fmt.Sprintf("%s\n%s\n%v\n%s\n%s", method, contentMD5, timestamp, nonce, canonicalizedResource)
	} else {
		stringToSign = fmt.Sprintf("%s\n%v\n%s\n%s", method, timestamp, nonce, canonicalizedResource)
	}
	key := []byte(appSecret)
	h := hmac.New(sha256.New, key)
	h.Write([]byte(stringToSign))
	return base64.StdEncoding.EncodeToString(h.Sum(nil))
}

// calculateContentMD5 calculates the MD5 hash of the content and returns it as a base64-encoded string
func calculateContentMD5(content []byte) string {
	hasher := md5.New()
	hasher.Write(content)
	md5Sum := hasher.Sum(nil)
	return base64.StdEncoding.EncodeToString(md5Sum)
}
