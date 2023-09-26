package main

import (
	"bytes"
	"com.clobotics/util"
	"encoding/json"
	"io"
	"mime/multipart"
	"os"
	"testing"
)

const (
	AppID     = "AppID"
	AppSecret = "AppSecret"
	Host      = "https://openapi.clobotics.com"
)

const (
	HttpPathIrRecognize = "/ir/recognize"
	HttpPathSceneList   = "/ir/scene/list"
	HttpPathUpload      = "/op/upload"
)

func TestGetSceneList(t *testing.T) {
	queryParams := map[string]string{
		// "name": "test",
	}
	req, err := util.CreateRequest(AppID, AppSecret, Host, util.HttpMethodGet, HttpPathSceneList, queryParams, []byte{})
	if err != nil {
		t.Fatal(err)
	}
	data, err := util.DoRequest(req)
	if err != nil {
		t.Fatal(err)
	}
	printSuccessData(t, data)
}

func TestCreateIrRecognize(t *testing.T) {
	var requestBody = []byte(`{
			  "request_id": "c0c80b91-694c-4f6e-8232-1a039a2731a0",
			  "image_url": "https://f-dev.clobotics.cn/4/0040ca4d85f5e5ef7354102f486ef843.jpg",
			  "scene_code": "104",
			  "processed_image_url": "https://f-dev.clobotics.cn/4/0040ca4d85f5e5ef7354102f486ef877.jpg",
			  "ext_info": "{\"key\":\"value\"}"
			}`)
	queryParams := map[string]string{}
	req, err := util.CreateRequest(AppID, AppSecret, Host, util.HttpMethodPost, HttpPathIrRecognize, queryParams, requestBody)
	if err != nil {
		t.Fatal(err)
	}
	data, err := util.DoRequest(req)
	if err != nil {
		t.Fatal(err)
	}
	printSuccessData(t, data)
}

func TestUploadFile(t *testing.T) {
	filePath := "/Users/ycw/Desktop/img3.png"
	file, err := os.Open(filePath)
	if err != nil {
		t.Fatal(err)
	}
	defer file.Close()
	var requestBody bytes.Buffer
	writer := multipart.NewWriter(&requestBody)
	fileField, err := writer.CreateFormFile("file", "img3.png")
	if err != nil {
		t.Fatal(err)
	}
	_, err = io.Copy(fileField, file)
	if err != nil {
		t.Fatal(err)
		return
	}
	writer.Close()
	req, err := util.CreateUploadRequest(AppID, AppSecret, Host, util.HttpMethodPost, HttpPathUpload, &requestBody)
	if err != nil {
		t.Fatal(err)
	}
	req.Header.Set("Content-Type", writer.FormDataContentType())
	data, err := util.DoRequest(req)
	if err != nil {
		t.Fatal(err)
	}
	printSuccessData(t, data)
}

func printSuccessData(t *testing.T, data interface{}) {
	dataContent, _ := json.Marshal(data)
	t.Logf("Success: %v", string(dataContent))
}
