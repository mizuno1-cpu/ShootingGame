@echo off
chcp 65001 > nul
echo ゲームを起動中...

rem binフォルダにコンパイル済みのクラスがある前提
java -Dfile.encoding=UTF-8 -cp bin Game

if %errorlevel% neq 0 (
    echo 起動に失敗しました。
    pause
    exit /b
)

echo ゲームを終了しました。
timeout /t 2 > nul
exit
