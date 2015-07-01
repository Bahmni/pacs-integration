# PACS-Integration


#####< IMPORTANT >

Atomfeed set the markers to first page if you don't set it. 

So, Set the markers manually after provisioning and before deployment.

Especially openmrs encounter feed as we are reading encounter feed to figure out the orders.

Use the following sql query to set the markers manually according to the events in your machine. 
(change the last_read_entry_id and feed_uri_for_last_read_entry )

insert into markers (feed_uri, last_read_entry_id, feed_uri_for_last_read_entry) 
    values ('http://loalhost:8080/openmrs/ws/atomfeed/encounter/recent', '?', '?');