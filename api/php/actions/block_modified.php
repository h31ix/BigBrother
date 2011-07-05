<?php

/** Block Modified Action

*/

class BlockModified extends Action
{
	public $removed = false;
	public function __construct($row,$wasRemoved)
	{
		parent::LoadData($row);
		$this->removed=$wasRemoved;
	}
	
	public function __toString()
	{
		return sprintf("%s %s a %s at World %s - &lt;%d,%d,%d&gt;",$this->getUserLink(),($this->removed) ? 'removed' : 'placed', blockID2Link($this->type), $this->getWorldName(),$this->X,$this->Y,$this->Z);
	}
	
	public function getActionString()
	{
		return (($this->removed) ? 'removed' : 'placed'). ' a '. blockID2Link($this->type);
	}
}

registerAction(BLOCK_BROKEN,function($row){
	return new BlockModified($row,true);
});
registerAction(BLOCK_PLACED,function($row){
	return new BlockModified($row,false);
});