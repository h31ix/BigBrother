<?php

class ButtonPressed extends Action
{
	public function __construct($row)
	{
		parent::LoadData($row);
	}
	
	public function __toString()
	{
		return sprintf("%s pressed a button at World %s - &lt;%d,%d,%d&gt;",$this->getUserLink(),$this->data, $this->getWorldName(),$this->X,$this->Y,$this->Z);
	}
	
	public function getActionString()
	{
		return 'pressed a button';
	}
}
registerAction(BUTTON_PRESSED,function($row){
	return new ButtonPressed($row,true);
});