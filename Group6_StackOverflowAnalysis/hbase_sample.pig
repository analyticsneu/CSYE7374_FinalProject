raw_data = LOAD 'sample_data.csv' USING PigStorage( ',' ) AS (
    listing_id: chararray,
    fname: chararray,
    lname: chararray );

B = foreach raw_data generate ($0,$1,$2)

result = order B by $0 DESC

dump

raw_data = LOAD 'sample_data.csv' USING PigStorage( ',' ) AS (
    listing_id: chararray,
    fname: chararray,
    lname: chararray );

B = foreach raw_data generate ($0,$2,$4)

result = order B by $0 DESC

dump

raw_data = LOAD 'sample_data.csv' USING PigStorage( ',' ) AS (
    listing_id: chararray,
    fname: chararray,
    lname: chararray );

B = foreach raw_data generate ($0,$2,$5)

result = order B by $0 DESC

dump

STORE raw_data INTO 'hbase://hello_world' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage (
    'info:fname info:lname');
     'info:fname info:lname');
     'info:fname info:lname');
     'info:fname info:lname');
)