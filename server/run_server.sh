nohup ./bin/run_server_A.sh 0<&- &> /tmp/app-a.log &
nohup ./bin/run_server_B.sh 0<&- &> /tmp/app-b.log &
nohup ./bin/run_server_C.sh 0<&- &> /tmp/app-c.log &
