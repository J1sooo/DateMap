#!/bin/bash

echo "current user: $(whoami)"
echo "current home directory: $HOME"

cd /home/ec2-user || exit 1

# S3에서 JAR 다운로드
echo "S3_BUCKET: $S3_BUCKET"
aws s3 cp "s3://$S3_BUCKET/app.jar" "app.jar" || exit 1
JAR_PATH="app.jar"

if [ ! -f "$JAR_PATH" ]; then
  echo "JAR file not found at $JAR_PATH"
  exit 1
fi

chmod 755 "$JAR_PATH"
chown ec2-user:ec2-user "$JAR_PATH"

# 기존 애플리케이션 종료
PID=$(pgrep -u ec2-user -f "java -jar $JAR_PATH")
if [ -n "$PID" ]; then
  echo "Stopping existing app (PID: $PID)..."
  kill "$PID"
  sleep 3
fi

# 로그 디렉토리 및 로그파일 설정
LOG_DIR="/home/ec2-user/weblog"
mkdir -p "$LOG_DIR"
LOG_FILE="$LOG_DIR/$(date '+%Y%m%d_%H%M').log"

# 애플리케이션 실행
echo "Starting app..."
nohup java -jar "$JAR_PATH" > "$LOG_FILE" 2>&1 &

# 서버 응답 체크
RES_CHK_URL="http://localhost:8080"
MAX_ATTEMPTS=300
ATTEMPT=0

while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
  HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$RES_CHK_URL")

  if [ "$HTTP_CODE" -eq 200 ] || [ "$HTTP_CODE" -eq 404 ]; then
    echo "Server responded with HTTP $HTTP_CODE after $ATTEMPT seconds"
    break
  fi

  echo "Waiting for server... ($ATTEMPT sec)"
  ATTEMPT=$((ATTEMPT + 1))
  sleep 1
done

if [ "$HTTP_CODE" -eq 200 ] || [ "$HTTP_CODE" -eq 404 ] || [ "$HTTP_CODE" -eq 302 ]; then
  echo "Deploy Success! jar: $JAR_PATH log: $LOG_FILE resp: $HTTP_CODE"
else
  echo "Deploy Failed! jar: $JAR_PATH log: $LOG_FILE resp: $HTTP_CODE"
fi
