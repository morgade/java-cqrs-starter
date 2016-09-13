(function(angular) {
   angular.module('tabs').controller("ChefTodoController", 
        ["$scope" ,"$resource", function($scope, $resource) {
            var chefResource = $resource("/chef", {}, { 
                todoList: { method:'get', isArray: true } 
            });
            var tabsResource = $resource("/tab/:id/:verb", {}, {
                markFoodPrepared: { method:'get', params: {'verb':'markFoodPrepared' } },
            });
            
            $scope.loadTodoList = function () {
                chefResource.todoList().$promise.then(function (result) {
                    $scope.todoListGroups = result;
                });  
            };
            
            $scope.markPrepared = function (group, item) {
                return tabsResource.markFoodPrepared({'id': group.tab, 'item': item.menuNumber })
                        .$promise
                        .then($scope.loadTodoList);   
            };
            
            $scope.loadTodoList();
        }]
    );
    
})
(angular);


