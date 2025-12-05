@echo off
chcp 65001 > nul
echo コンパイル中...
javac -encoding UTF-8 -d bin -sourcepath src src/*.java > compile_log.txt 2>&1

if %errorlevel% neq 0 (
    echo コンパイルに失敗しました。詳細は compile_log.txt を確認してください。
    timeout /t 5 > nul
    exit /b
)

echo コンパイル完了！
timeout /t 2 > nul
exit