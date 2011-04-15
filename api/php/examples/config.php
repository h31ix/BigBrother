<?php

/////////////////////////////////////////////
/*

  For this application to work, you must set 
  some variables here.

*/
/////////////////////////////////////////////

$BB_API = 'api/php/';         //Path to BigBrother PHP api

$MYSQL_HOST = 'host';         
$MYSQL_USER = 'user';
$MYSQL_PASS = 'pass';
$MYSQL_DB = 'db';

////////// NOTHING ELSE TO DO HERE

$conn = mysql_connect($MYSQL_HOST,$MYSQL_USER,$MYSQL_PASS)
or die ("Can't connect to MySQL!");
mysql_select_db($MYSQL_DB,$conn)
or die ("Can't change database!");
?>
