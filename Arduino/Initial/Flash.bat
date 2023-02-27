@echo off
SET swName="AudiDoorHandleGen.ino.hex"
SET /p COM="Connect to COM port:"
avrdude.exe -C avrdude.conf -v -patmega2560 -cwiring -P COM%COM% -b115200 -D -U flash:w:%swName%:i
echo "The file %swName% was witten successfully"