(function(angular) {
   angular.module('tabs').controller("ChefTodoController", 
        ["$scope" ,"$resource", function($scope, $resource) {
            // chef resource endpoint
            var chefResource = $resource("/chef", {}, { 
                todoList: { method:'get', isArray: true } 
            });
            // Main tabs resource endpoint
            var tabsResource = $resource("/tab/:id/:verb", {}, {
                markFoodPrepared: { method:'get', params: {'verb':'markFoodPrepared' } },
            });
            
            /**
             * Loads todo list
             */
            $scope.loadTodoList = function () {
                chefResource.todoList().$promise.then(function (result) {
                    $scope.todoListGroups = result;
                });  
            };
            
            /**
             * Sens mark prepared commands
             */
            $scope.markPrepared = function (group, item) {
                return tabsResource.markFoodPrepared({'id': group.tab, 'item': item.menuNumber })
                        .$promise
                        .then($scope.loadTodoList);   
            };
            
            $scope.loadTodoList();
        }]
    );
    
})(angular);


