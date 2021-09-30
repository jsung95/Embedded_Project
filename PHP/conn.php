<?php

#SQL접근
$con = mysqli_connect("localhost", "sql_user", "암호", "sql_db"); 

#에러처리
if (mysqli_connect_errno($con))
{
    echo "fail connect : " . mysqli_connect_error();
}

#utf-8설정
mysqli_set_charset($con, "utf8");

#SQL쿼리문
#가장 최근데이터 3개 출력 -> 오름차순 정렬
$res = mysqli_query($con, "select A.* from (select * from collect_data order by collect_time desc LIMIT 3) A order by A.collect_time asc"); 


#변수 result에 DB값 대입
$result = array();
while($row = mysqli_fetch_array($res)){
    array_push($result, 
        array('sensor'=>$row[0], 
              'collect_time'=>$row[1], 
              'value1'=>$row[2], 
              'value2'=>$row[3]
              )
            );
}

#result 값을 JSON으로 인코딩
echo json_encode(array("result"=>$result), JSON_UNESCAPED_UNICODE); 

mysqli_close($con);

?>
