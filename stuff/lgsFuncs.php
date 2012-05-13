<? 

// Make a database connection.
if(!$db = @mysql_connect("mysql5.lichtographie.de", "xxxxx", "yyyyyy"))
        die('<b>An Error Occured</b><br>I was unable to connect to the database.');
if(!@mysql_select_db("db214075_57",$db))
        die("<b>An Error Occured</b><br>I was unable to find the database on your MySQL server.<br>");
		
function get_albums(){
	global $db;
	
	$sql = "SELECT id, name FROM albums ORDER BY name ";
	if(!$result = mysql_query($sql, $db)) die("<b>Fehler: </b><br>$sql"); 
	while($row = mysql_fetch_array($result)){
		print  $row[id]." ; ".$row[name]."\n";
	}
}

function get_album_files($albumid){
	global $db;
	
	$sql = "SELECT file FROM albums_content WHERE albums_id=".$albumid;
	if(!$result = mysql_query($sql, $db)) die("<b>Fehler: </b><br>$sql"); 
	while($row = mysql_fetch_array($result)){
		print  $row[file]."\n";
	}
}

$album= $_GET['album'];
if($album){
	get_album_files($album);
} else{
	get_albums();
}
?>
