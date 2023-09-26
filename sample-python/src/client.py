import requests, hmac, base64, hashlib, time, uuid, json
from urllib.parse import urlparse
from urllib.parse import unquote

openapi_host = "https://openapi-dev.clobotics.cn"
openapi_app_id = "<app_id>"
openapi_app_secret = "<app_secret>"
exclude_content_md5_urls = {"/op/upload"}

# 生成签名
def generate_signature(string_to_sign) -> str:
    signature = hmac.new(openapi_app_secret.encode("utf-8"), string_to_sign.encode("utf-8"),  digestmod=hashlib.sha256)
    bb = signature.digest()
    return "cbs:" + openapi_app_id + ":" + base64.b64encode(bb).decode("utf-8")
# 发送请求
def send_request(method, path, body = None, data = None, files = None):
    request_url = openapi_host + path
    # 构造参数和待签名字符串
    content_md5 = ""

    timestamp_str = str(int(time.time()))
    nonce_str = uuid.uuid4().hex
    url_result = urlparse(request_url)
    query_params_string: str = url_result.query
    if (method == "POST" or method == "PUT") and url_result.path not in exclude_content_md5_urls:
        json_body = ('{}'.format(json.dumps(body))).encode("utf-8")
        content_md5 = base64.b64encode(hashlib.md5(json_body).digest()).decode("utf-8")

    # 将排好序的query参数拼接起来，并用&符号连接起来作为CanonicalizedResource部分内容
    canonicalized_resource = url_result.path
    if query_params_string != "" :
        # 对所有query参数按照名称排序（a-z升序）
        sorted_query_param_list = sorted(query_params_string.split("&"))
        decoded_query_param_list = list()
        for query_param in sorted_query_param_list :
            # 解码query参数
            param_kv = query_param.split("=")
            decoded_query_param_list.append(param_kv[0] + "=" + unquote(param_kv[1]))
        canonicalized_resource += "?" + "&".join(decoded_query_param_list)
    if content_md5 != "" :
        # 拼接StringToSign字符串
        string_to_sign="{}\n{}\n{}\n{}\n{}".format(method, content_md5, timestamp_str, nonce_str, canonicalized_resource )
    else :
        string_to_sign="{}\n{}\n{}\n{}".format(method, timestamp_str, nonce_str, canonicalized_resource )

    # 生成签名
    signature=generate_signature(string_to_sign)

    headers={
        'Authorization': signature,
        'Timestamp': timestamp_str,
        'Nonce': nonce_str }

    if method == "POST" or method == "PUT":
        return requests.request(method, url=request_url, json=body, headers=headers, data=data, files=files)
    else:
        return requests.request(method, url=request_url, headers=headers)