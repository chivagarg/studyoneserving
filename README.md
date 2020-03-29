To add new entries to the DisplayWord table:
```$xslt
curl -X POST http://localhost:8080/v1/words/create -H "Content-Type:application/json" -d "{\"word\":\"justintime\", \"meaning\":\"for dinner\"}"

```
To get all entries:
```$xslt
curl http://localhost:8080/v1/words/all
```
