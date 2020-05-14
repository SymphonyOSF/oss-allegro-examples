
  var tableRef = document.getElementById('blotter').getElementsByTagName('tbody')[0];
  var heartbeatRef = document.getElementById('heartbeat');
  var errorRef = document.getElementById('error');
  
  function heartBeat(time)
  {
	 heartbeatRef.innerHTML = time; 
  }
  
  function showError(message)
  {
	 errorRef.innerHTML = message; 
  }
  
  function addCol(newRow, text)
  {
    // Insert a cell in the row at index 0
    var newCell  = newRow.insertCell(-1);
    // Append a text node to the cell
    var newText  = document.createTextNode(text);
    newCell.appendChild(newText);
  }
  
  function upsert(newRow, storedObject)
  {
    addCol(newRow, storedObject.sortKey);
    
    if(storedObject.header == null)
  	  addCol(newRow, '');
    else
    	addCol(newRow, storedObject.header._type);
  }
  
  function deletePayload(baseHash)
  {
	var row = document.getElementById(baseHash);
	
	if(row != null)
	{
		tableRef.deleteRow(row.rowIndex);
	}
  }

  function upsertPayload(baseHash, storedObject, payload)
  {
	var row = document.getElementById(baseHash);
	
	if(row == null)
	{
	    // Insert a row in the table at the last row
	    var newRow   = tableRef.insertRow(-1);
	    
	    newRow.id = baseHash;
	    upsert(newRow, storedObject);
	    
	    addCol(newRow, payload._type);
	    addCol(newRow, payload.description);
	}
	else
	{
		row.cells[0].innerHTML = storedObject.sortKey;
		
		if(storedObject.header == null)
			row.cells[1].innerHTML = storedObject.sortKey;
		else
			row.cells[1].innerHTML = storedObject.header._type;
		
		row.cells[2].innerHTML = payload._type;
		row.cells[3].innerHTML = payload.description;
	}
  }
  
//  function upsertException(storedObject, exception)
//  {
//    // Insert a row in the table at the last row
//    var newRow   = tableRef.insertRow(-1);
//    upsert(newRow, storedObject);
//    addCol(newRow, exception);
//  }