<? 

require 'config.php';
include $BB_API.'action.php';

//  WARNING: I can't guarantee, that the example is secured against SQL injection. So, I recommend this only for private (admins/mods) use. 
//
//  SCRIPT PARAMETERS
//  world = number of world to view (default:0)
//  hours = how many hours of logging to view? (default: 3)
//  from  = ...starting "from"-hours from now
//                                                 
// Author: AirGuru <airguru@eastforce.net>

//Fetch playerlist
$player = array();

$query = mysql_query("SELECT * FROM bbusers",$conn);
while($result = mysql_fetch_array($query))
{
   $player[$result["id"]] = $result["name"];
}

//args
$world = intval($_GET['world']); // World ID #
$hours = intval($_GET['hours']); // Hours ago?
$from = intval($_GET['from']); // idk

//some time computation
$now = time();
if($from<$hours)  {
$start = $now - 60*60*$hours;
$end = $now;
}
else
{
$start = $now - 60*60*$from;
$end = $start + 60*60*$hours;
}

$query = mysql_query('SELECT * FROM bbdata where world='.$world.' and type=54 and date>'.$start.' and date<'.$end.' order by date desc',$conn);

$actions = array();
$i = 0;

while($result = mysql_fetch_array($query))  {
  $actions[$i] = Action::FromData($result);
  $i++;
}


?>
<html>
  <head>
    <title>Chest access log</title>
  </head>

<body>
<a href="chestview.php?<?php echo 'world='.$world.'&amp;hours='.$hours.'&amp;from='.($from+$hours); ?>">Previous <?php echo $hours;?> hours</a>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<?php if($from>$hours) {?>
<a href="chestview.php?<?php echo 'world='.$world.'&amp;hours='.$hours.'&amp;from='.($from-$hours); ?>">Next <?php echo $hours;?> hours</a>
<?php } ?>
<table border="2" cellpadding="10">
   <thead><th>Time</th><th>Player</th><th>Coords</th><th>Change</th></thead>
<?php

foreach($actions as $i => $action)
{
  if ($action->changed) {
    $o = '<tr>';
    $o .= '<td>'.date('d.m - H:i:s ',$action->date).'</td>';
    $o .= '<td>'.$player[$action->player].'</td>';
    $o .= '<td>'.$action->X.','.$action->Y.','.$action->Z.'</td>';
    
    $o .= '<td>';
    $modif = $action->getModifications();
    foreach($modif as $id => $count)  {
      if($count!=0) {
        $o .= $count.' '.$items[$id].'<br />';
      }
    }
    
    $o .= '</td></tr>';
    
    echo $o;
  }
}

?>
</table>
</body>
</html>
