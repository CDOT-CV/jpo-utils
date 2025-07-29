CONNECT_URL="http://localhost:8083" 
# Get the list of connectors 
connectors=$(curl -s "$CONNECT_URL/connectors") 

# Remove brackets from the JSON array 
connectors=${connectors:1:-1} 
# Split the connectors into an array 
IFS=', ' read -r -a connector_array <<< "$connectors" 
# Loop through each connector and delete it 
for connector in "${connector_array[@]}"; do 
	connector_name=$(echo "$connector" | tr -d '"') 
	echo "Deleting connector: $connector_name" 
	curl -X DELETE "$CONNECT_URL/connectors/$connector_name" >/dev/null 2>&1 
done 
echo "All connectors deleted." else echo "No connectors found." 