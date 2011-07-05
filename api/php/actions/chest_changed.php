<?php

class ChestChanged extends Action
{
	public $removed = false;
	public $changed = false;
	private $modif = array();
	
	public function __construct($row)
	{
		parent::LoadData($row);
		$this->parseData();
	}
	
	public function __toString()
	{
		return sprintf("%s changed chest at World %s - &lt;%d,%d,%d&gt;",$this->getUserLink(), $this->world,$this->X,$this->Y,$this->Z);
	}
	
	public function getActionString()
	{
		return 'changed chest (D: '.$this->data.'; T: '.$this->type.')';
	}

	public function getModifications()
	{
		return $this->modif;
	}

	//Counts total changes in chest contents. Ignores just moving stuff around.
	//Returns an array, indexed by item ids.
	//Values are positive for items added to chest, negative for removed.
	private function parseData()
	{    
		$this->changed = false;
		$data = $this->data;
		$data = substr($data,1);   //get rid of {
		$modif = array();
		if (strlen($data)>1) {
			$changes = preg_split('/;/',$data);
			foreach ($changes as $id => $change){
				if(strlen($change)>1) {
					if (preg_match('/=/',$change)>0)  {
						//Item was replaced
						$split = preg_split('/=/',$change);
						$dataRemoved = preg_split('/:/',$split[0]);
						$dataAdded = preg_split('/:/',$split[1]);

						if (isset($modif[$dataRemoved[1]])) {
							$modif[$dataRemoved[1]] -= intval($dataRemoved[2]);
						} else { 
							$modif[$dataRemoved[1]] = -intval($dataRemoved[2]); 
						}

						if (isset($modif[$dataAdded[0]])) {  
							$modif[$dataAdded[0]] += intval($dataAdded[1]);
						} else { 
							$modif[$dataAdded[0]] = intval($dataAdded[1]); 
						}
					} else {
						//Item was added or removed
						$data = preg_split('/:/',$change);
						if (isset($modif[$data[1]])) {
							$modif[$data[1]] += intval($data[2]);
						} else { 
							$modif[$data[1]] = intval($data[2]); 
						}
					} 
				}
			}

			//check whether there were any modifications
			foreach($modif as $id => $value)
			{
				if ($value != 0)
				{
					$this->changed = true;
				}
			}
			$this->modif = $modif;
		}
	}
}
registerAction(CHEST_DELTA,function($row){
	return new ChestChanged($row,false);
});