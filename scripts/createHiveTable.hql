CREATE EXTERNAL TABLE real_estate_transactions(
street string,
zip string,
state string,
beds string,
baths string,
sq__ft string,
type string,
sale_date string,
price string,
latitude string,
longitude string)
PARTITIONED BY (city string)
STORED AS PARQUET
LOCATION '';