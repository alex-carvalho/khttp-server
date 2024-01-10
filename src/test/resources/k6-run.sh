
echo "ioThreadPoolServer"
k6 run -e PORT=8080 load-test.js

echo "ioVirtualThreadPool"
k6 run -e PORT=8081 load-test.js

echo "nioServer"
k6 run -e PORT=8082 load-test.js