#! /bin/bash
NOW=$(date +"%Y%m%d_%H%M")
#PY3='stdbuf -o0 nohup python3 -u'
mkdir -p logs

#stdbuf -o0 nohup th main.lua  -file books-10M.t7 -conf conf/conf.movieLens.10M.V.lua  -save network.t7 -type V -meta 0 -gpu 1  > "./logs/book_recommendation$NOW.log" &
stdbuf -o0 nohup th main.lua  -file books-10M.t7 -conf conf.movieLens.10M.U.lua  -save network.t7 -type U -meta 0 -gpu 1  > "./logs/book_recommendation$NOW.log" &


