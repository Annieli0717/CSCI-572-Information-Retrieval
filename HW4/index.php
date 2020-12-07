<?php

// make sure browsers see this page as utf-8 encoded HTML
header('Content-Type: text/html; charset=utf-8');

$limit = 10;
$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
$results = false;
if ($query){
  $algo = isset($_REQUEST['sort'])? $_REQUEST['sort'] : "Lucene";
  // The Apache Solr Client library should be on the include path
  // which is usually most easily accomplished by placing in the
  // same directory as this script ( . or current directory is a Lucene
  // php include path entry in the php.ini)
  require_once('solr-php-client-master/Apache/Solr/Service.php');
  // create a new solr service instance - host, port, and webapp
  // path (all Lucenes in this example)
  $solr = new Apache_Solr_Service('localhost', 8983, '/solr/myexample');
  // if magic quotes is enabled then stripslashes will be needed
  if (get_magic_quotes_gpc() == 1){
    $query = stripslashes($query);
  }
  // in production code you'll always want to use a try /catch for any
  // possible exceptions emitted  by searching (i.e. connection
  // problems or a query parsing error)
  try{
    if($algo != "Lucene"){
      $additionalParameters=array('sort' => 'pageRankFile desc');
    }else{
      $additionalParameters =array('sort' => '');
    }
    $results = $solr->search($query, 0, $limit, $additionalParameters);
  }
  catch (Exception $e){
    // in production you'd probably log or email this error to an admin
    // and then show a special message to the user but for this example
    // we're going to show the full exception
    die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
  }
}

?>
<html>
  <head>
    <title>HW4: Lucene  vs.  PageRank </title>
  </head>
  <body>
    <center>
    <h1>Lucene  vs.  PageRank</h1>
    <form  accept-charset="utf-8" method="get" >
      <label for="q">Search:</label>
      <input id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>"/>
      <input type="submit" value="Submit"/>
<br/>
<br/>
    <input type="radio" name="sort" value="Lucene" <?php if(isset($_REQUEST['sort']) && $algo == "Lucene") { echo 'checked="checked"';} ?>>Lucene
    <input type="radio" name="sort" value="pagerank" <?php if(isset($_REQUEST['sort']) && $algo == "pagerank") { echo 'checked="checked"';} ?>>Page Rank    
    </form>
    </center>
<?php
$arrayFromCSV =  array_map('str_getcsv', file('LATIMES/URLtoHTML_latimes_news.csv'));
if ($results){
  $total = (int) $results->response->numFound;
  $start = min(1, $total);
  $end = min($limit, $total);
  
  echo "  <div>Results $start -  $end of $total :</div> <ol>";
  echo "<table border>";
  $counter = 1;
  foreach ($results->response->docs as $doc){  
    echo "<tr>";
    echo "<td style='padding-right:40px;'>" . $counter . "</td>";
    echo "<td>";
    $id = $doc->id;
    $title = $doc->title;
    $desc = $doc->description;

    if($title=="" ||$title==null){
      $title = $doc->dc_title;
      if($title=="" ||$title==null)
        $title="N/A";
    }
    if($desc=="" ||$desc==null){
       $desc="N/A";
    }
   
    $id2 = $id;
    $id = str_replace("/Users/lidunxuan/Desktop/CS 572/HW4/solr-7.7.3/latimes/","",$id);
    foreach($arrayFromCSV as $row1){
      if($id==$row1[0]){
        $url = $row1[1];
        break;
      }
    }
    unset($row1);
    echo "Title       : <a href = '$url' target='_blank'>$title</a></br>";
    echo "URL         : <a href = '$url' target='_blank'>$url</a></br>";
    echo "ID          : $id2 </br>";
    echo "Description : $desc</br>";
    echo "</td>";
    $counter = $counter + 1;
  }
  
  echo "</ol></table>";
}
?>

  </body>
</html>