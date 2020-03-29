To add new entries to the DisplayWord table:
```$xslt
curl -X POST http://localhost:8080/v1/words/create -H "Content-Type:application/json" -d "{\"word\":\"justintime\", \"meaning\":\"for dinner\"}"

```
To get all entries:
```$xslt
curl http://localhost:8080/v1/words/all
```

JWT authentication:
GET without a token:
```$xslt
curl -X GET http://localhost:8080/me
{"timestamp":"2020-03-29T22:19:14.047+0000","status":403,"error":"Forbidden","message":"Access Denied","path":"/me"}
```

Fetching token via signin
```$xslt
curl -X POST http://localhost:8080/auth/signin -H "Content-Type:application/json" -d "{\"username\":\"admin\", \"password\":\"password\"}"

{"username":"admin","token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIlJPTEVfVVNFUiIsIlJPTEVfQURNSU4iXSwiaWF0IjoxNTg1NTIwMjUzLCJleHAiOjE1ODU1MjM4NTN9.o99tMoSo8SUAWwnD7EfZW2HxyVgFgO3RcMQdS9iNYEM"}
```

Get with token from sign in call:
```$xslt
curl -X GET http://localhost:8080/me -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIlJPTEVfVVNFUiIsIlJPTEVfQURNSU4iXSwiaWF0IjoxNTg1NTE5OTU5LCJleHAiOjE1ODU1MjM1NTl9.yf_mjhv7M1PFog6hKnTkKxrU6maeGy8scCsrrY8Wlo4"
{"roles":["ROLE_USER","ROLE_ADMIN"],"username":"admin"}
```