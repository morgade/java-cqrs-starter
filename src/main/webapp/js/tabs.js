(function(angular) {
   var tabs = angular.module('tabs', ['ngResource', 'ngRoute'] );
   
    tabs.config(['$routeProvider', function($routeProvider) {
         $routeProvider.when('/waiter', {
             templateUrl: 'waiter.html',
             controller: 'WaiterController'
         });
         
         $routeProvider.when('/chef-todo', {
             templateUrl: 'chef-todo.html',
             controller: 'ChefTodoController'
         });
         
         $routeProvider.otherwise({redirectTo: '/waiter'});
    }]);
    
})(angular);