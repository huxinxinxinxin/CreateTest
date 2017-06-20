#!/usr/bin/expect
set username [lindex $argv 0]
set hostname [lindex $argv 1]
set password [lindex $argv 2]
spawn ssh $username@$hostname
expect "*password*"
send "$password\r"
interact

