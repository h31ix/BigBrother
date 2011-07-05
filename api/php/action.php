<?php
/** BBWeb Action Processing Class

Blah blah blah BSD License.
*/

require('constants.php');

require('actions/block_modified.php');
require('actions/button_pressed.php');
require('actions/chatted.php');
require('actions/chest_changed.php');
require('actions/chest_opened.php');
require('actions/command_sent.php');
require('actions/disconnected.php');
require('actions/door_opened.php');
require('actions/exploded.php');
require('actions/fire_lit.php');
require('actions/lever_switched.php');
require('actions/logged_in.php');
require('actions/sign_modified.php');
require('actions/teleported.php');
require('actions/tnt_exploded.php');

class Action
{
	public $id=0;
	public $date=0;
	public $player='';
	public $world=0;
	public $X=0; 	
	public $Y=0; 	
	public $Z=0;
	public $type=0;
	public $data='';
	public $rbacked=false;
	
	// 0 id 
	// 1 date
	// 2 player
	// 3 action
	// 4 world
	// 5 x
	// 6 y
	// 7 z
	// 8 type
	// 9 data
	// 10 rbacked
	public function LoadData($row){
		$this->id=intval($row[0]);
		$this->date=intval($row[1]);
		$this->player=intval($row[2]);
		$this->actionID=intval($row[3]);
		$this->world=intval($row[4]);
		$this->X=intval($row[5]); 	
		$this->Y=intval($row[6]); 	
		$this->Z=intval($row[7]);
		$this->type=intval($row[8]);
		$this->data=$row[9];
		$this->rbacked=intval($row[10])==1;
	}
	
	public static function FromData($row)
	{
		global $gActionHandlers;
		$aID=intval($row[3]);
		return $gActionHandlers[$aID]($row);
	}
	
	public static function toWidget($stats)
	{
		$o='<table><thead><th>Time</th><th>Player</th><th>Action</th><th>World</th><th>X</th><th>Y</th><th>Z</th></thead>';
		$i=0;
		foreach($stats as $stat)
		{
			$o.='<tr class="'.(($i++%2==0)?'tr_even':'tr_odd').' act_'.$stat->actionID.'">';
			$o.='<td>'.date('M.d@h:i:s A',$stat->date).'</td>';
			$o.='<td>'.$stat->getUserLink().'</td>';
			$o.='<td>'.$stat->getActionString().'</td>';
			$o.='<td>'.$stat->getWorldName().'</td>';
			$o.='<td>'.$stat->X.'</td>';
			$o.='<td>'.$stat->Y.'</td>';
			$o.='<td>'.$stat->Z.'</td>';
			$o.='</tr>';
		}
		$o.='</table>';
		return $o;
	}
	
	public function getActionString()
	{
		return $this->actionID.' doesn\'t have an action string yet!';
	}
	
	public function getWorldName()
	{
		global $gWorlds;
		return $gWorlds[$this->world];
	}
	
	public function getPos()
	{
		return sprintf('World (#%d) - &lt;%d,%d,%d&gt;',$this->getWorldName(), $this->world,$this->X,$this->Y,$this->Z);
	}
}

public function registerAction($id,$action) {
	global $gActionHandlers;
	$gActionHandlers[$id]=$action;
}