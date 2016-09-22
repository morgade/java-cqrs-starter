var params;
var statusFunc = function(err, data) {
    if (err) ppJson(err); // an error occurred
    else ppJson(data); // successful response
};

dynamodb.deleteTable({
    TableName: "tab_evt",
}, statusFunc);

dynamodb.createTable({
    TableName : "tab_evt",
    KeySchema: [       
        { AttributeName: "id", KeyType: "HASH" },  //Partition key
        { AttributeName: "idx", KeyType: "RANGE" }  //Sort key
    ],
    AttributeDefinitions: [       
        { AttributeName: "id", AttributeType: "S" },
        { AttributeName: "idx", AttributeType: "N" }
    ],
    ProvisionedThroughput: {       
        ReadCapacityUnits: 1, 
        WriteCapacityUnits: 1
    }
}, statusFunc);