#!/usr/bin/expect
trap {
 set rows [stty rows]
 set cols [stty columns]
 stty rows $rows columns $cols < $spawn_out(slave,name)
} WINCH
set username [lindex $argv 0]
set hostname [lindex $argv 1]
set password [lindex $argv 2]
spawn ssh $username@$hostname
expect "*password*"
send "$password\r"
interact

