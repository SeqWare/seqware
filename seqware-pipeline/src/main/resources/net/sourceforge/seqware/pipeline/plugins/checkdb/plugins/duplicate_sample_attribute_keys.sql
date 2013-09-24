SELECT f.sw_accession, tag, count(value) AS count from sample_attribute fa 
JOIN sample f ON f.sample_id = fa.sample_id 
GROUP BY f.sw_accession, tag
HAVING count(value) > 1
ORDER BY count DESC ;