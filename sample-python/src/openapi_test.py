from client import send_request

if __name__ == "__main__":
    url = "/user?name=http%3A%2F%2Fwww.baidu.com&username=user123"
    response = send_request("GET", url)
    if response.status_code == 200:
        print(response.text)
    else:
        print(response.status_code)
    url = "/user"
    body = {"password": "user123", "username": "user123"}
    response = send_request("POST", url, body)
    if response.status_code == 200:
        print(response.text)
    else:
        print(response.status_code)
    file = {"file": open("../../shelf.png", "rb")}
    url = "/op/upload"
    data = {"watermark":'''{
  "data": [
    "Task Name: Zengcheng Store 1",
    "Store Name: Zengcheng Store 1",
    "Store Code: zz_001",
    "Sales Representative: Xiaoming",
    "Time: 2023-07-19 16:30:00",
    "Store Address: xxxxx, Zengcheng District, Guangzhou City"
  ],
  "separator": "auto"
}'''}
    response = send_request("POST", url,data = data, files=file)
    if response.status_code == 200:
        print(response.text)
    else:
        print(response.status_code)
