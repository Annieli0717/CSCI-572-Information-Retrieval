<?php
ini_set('memory_limit','-1');
include 'SpellCorrector.php';
include 'simple_html_dom.php';
header('Content-Type: text/html; charset=utf-8');
$div=false;
$correct = "";
$correct1="";
$output = "";
$limit = 10;
$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
$results = false;
if ($query)
{
  $choice = isset($_GET['search'])? $_GET['search'] : "lucene";
  require_once('solr-php-client-master/Apache/Solr/Service.php');
  $solr = new Apache_Solr_Service('localhost', 8983, '/solr/myexample');
  if( ! $solr->ping())
    echo 'Solr service is not available'; 
  if (get_magic_quotes_gpc() == 1)
    $query = stripslashes($query);
  try
  {
    if($choice == "lucene")
      $additionalParameters=array('sort' => '');
    else
      $additionalParameters=array('sort' => 'pageRankFile desc');
    $word = explode(" ",$query);
    $spell = $word[sizeof($word)-1];
    for($i=0;$i<sizeOf($word);$i++){
      ini_set('memory_limit',-1);
      ini_set('max_execution_time', 300);
      $che = SpellCorrector::correct($word[$i]);
      if($correct!="")
        $correct = $correct."+".trim($che);
      else
        $correct = trim($che);
      $correct1 = $correct1." ".trim($che);
    }
    $correct1 = str_replace("+"," ",$correct);
    $div=false;
    if(strtolower($query)==strtolower($correct1))
    {
      $results = $solr->search($query, 0, $limit, $additionalParameters);
    }
    else
    {
      $div =true;
      $results = $solr->search($query, 0, $limit, $additionalParameters);
      $link = "http://localhost:8000/index.php?q=$correct&sort=$choice";
      $output = "Did you mean: <a href='$link'><i>$correct1</i></a>?";
    }
  }
  catch (Exception $e)
  {
    die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
  }
}
?>
<html>
  <head>
    <title>Solr Search Engine</title>
    <link rel="stylesheet" href="http://code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
    <script src="http://code.jquery.com/jquery-1.10.2.js"></script>
    <script src="http://code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
  </head>
  <body style='margin-left:30px; margin-right:30px;'>
    <br>
    <h1 align="center" style="margin: 5px">Solr Search Engine</h1>
    <br>
    <form accept-charset="utf-8" method="get" id="searchform" align="center">
      <label for="q">Search:</label>
      <input id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>" autocomplete="off"/>
      <datalist id="searchresults"></datalist>
      <input type="hidden" name="spellcheck" id="spellcheck" value="false"> 
      <input type="submit" value="Submit"/>
      <br>
      <div style="padding: 10px">
        <input type="radio" name="search" <?php echo 'checked="checked"';?>  value="lucene" /> Lucene
      <input type="radio" name="search" <?php if (isset($_GET['search']) && $_GET['search']=="pagerank") echo 'checked="checked"';?> value="pagerank" /> PageRank <br>
      </div>
      
    </form>
    <script>
     $(function() {
       var URL_PREFIX = "http://localhost:8983/solr/myexample/suggest?q=";
       var URL_SUFFIX = "&wt=json&indent=true";
       var count=0;
       var tags = [];
       $("#q").autocomplete({
         source : function(request, response)
         {
           var correct="",before="";
           var query = $("#q").val().toLowerCase();
           var character_count = query.length - (query.match(/ /g) || []).length;
           var space =  query.lastIndexOf(' ');
           if(query.length-1>space && space!=-1)
           {
            correct=query.substr(space+1);
            before = query.substr(0,space);
          }
          else{
            correct=query.substr(0); 
          }
          var URL = URL_PREFIX + correct+ URL_SUFFIX;
          console.log(URL); 
          $.ajax({
           url : URL,
           success : function(data)
           {
              var js =data.suggest.suggest;
              var docs = JSON.stringify(js);
              var jsonData = JSON.parse(docs);
              var result =jsonData[correct].suggestions;
              var j=0;
              var stem =[];
              for(var i=0;i<5 && j<result.length;i++,j++){
              if(result[j].term==correct)
              {
                i--;
                continue;
              }
              for(var k=0;k<i && i>0;k++){
                if(tags[k].indexOf(result[j].term) >=0)
                {
                  i--;
                  continue;
                }
              }
              if(result[j].term.indexOf('.')>=0 || result[j].term.indexOf('_')>=0)
              {
                i--;
                continue;
              }
              var s =(result[j].term);
              if(stem.length == 5)
                break;
              if(stem.indexOf(s) == -1)
              {
                stem.push(s);
                if(before==""){
                  tags[i]=s;
                }
                else
                {
                  tags[i] = before+" ";
                  tags[i]+=s;
                }
                }
              }
              console.log(tags);
              response(tags);
              },
              dataType : 'jsonp',
              jsonp : 'json.wrf'
              });
          },
          minLength : 1
        })
     });
 </script>
<?php
if ($div){
  echo "<div style='color:#ff0000;font-size:18px;'><b>".$output."</b></div>";
}else{
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
    echo "Title : <a href = '$url' target='_blank'>$title</a></br>";
    echo "URL : <a href = '$url' target='_blank'>$url</a></br>";
    echo "ID: $id2 </br>";
    echo "Description: $desc</br>";
    echo "</td>";
    $counter = $counter + 1;
  }
  
  echo "</ol></table>";
}
}

?>

  </body>
</html>