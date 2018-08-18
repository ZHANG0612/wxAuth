<%@ page contentType="text/html;charset=UTF-8"%>
<html lang="en">
<head>
    <meta http-equiv="CONTENT-TYPE" content="text/html"; charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <form action="/WxAuth/wxCallBack" method="post">
    <input type="text" name="account" />
    <input type="passsword" name="password" />
    <input type="hidden" name="nickname" value="${nickname }" />
    <input type="hidden" name="openid" value="${openid }" />
    <input type="submit" value="登录并绑定" />
    </form>
</body>
</html>

