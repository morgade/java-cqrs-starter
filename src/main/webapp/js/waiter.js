(function(angular) {
   angular.module('tabs').controller("WaiterController", 
        ["$scope" ,"$resource", "$filter", "$window", function($scope, $resource, $filter, $window) {
            // menu resource endpoint
            var menuResource = $resource("/menu", {}, { 
                get: { method:'get', isArray: true } 
            });
            // staff resource endpoint
            var staffResource = $resource("/staff", {}, { 
                get: { method:'get', isArray: true } 
            });
            // table resource endpoint
            var tableResource = $resource("/table/:tableNumber/:verb", {}, {
                status: { method:'get', params: {'verb':'status' } },
                invoice: { method:'get', params: {'verb':'invoice' } }
            });
            // Main tabs resource endpoint
            var tabsResource = $resource("/tab/:id/:verb", {}, {
                tableNumbers: { method:'get', params: {'verb':'table-numbers' }, isArray: true },
                open: { method:'get', params: {'verb':'open' } },
                place: { method:'get', params: {'verb':'place' } },
                markDrinksServed: { method:'get', params: {'verb':'markDrinksServed' } },
                markFoodServed: { method:'get', params: {'verb':'markFoodServed' } },
                close: { method:'get', params: {'verb':'close' } }
            });

            /**
             * Change state to show new tab form
             */
            $scope.showNewTab = function() {
                $scope.tableNumber = null;
                $scope.newTab = true;
            };

            /**
             * Sends open tab command
             */
            $scope.openTab = function() {
                $scope.newTab = false;
                return tabsResource.open({'tableNumber': $scope.tableNumber, 'waiter': 'john'})
                        .$promise
                        .then($scope.commandFeedbackHandler($scope.loadTableData));
            };
            
            /**
             * Sends place order command
             */
            $scope.place = function() {
                var item = $scope.menuItem.menuNumber;
                $scope.menuItem = null;
                return tabsResource.place({'id': $scope.tableStatus.tabId, 'item':item})
                        .$promise
                        .then($scope.commandFeedbackHandler($scope.loadTableData));
            };
            
            /**
             * Sends markDrinksServed or markFoodServed command
             */
            $scope.markServed = function (item) {
                var menuItemToServe = $filter('filter')($scope.menu, {menuNumber: item.menuNumber})[0];
                if (menuItemToServe.drink) {
                    return tabsResource.markDrinksServed({'id': $scope.tableStatus.tabId, 'item':item.menuNumber})
                            .$promise
                            .then($scope.commandFeedbackHandler($scope.loadTableData));   
                } else {
                    return tabsResource.markFoodServed({'id': $scope.tableStatus.tabId, 'item':item.menuNumber})
                            .$promise
                            .then($scope.commandFeedbackHandler($scope.loadTableData));   
                }
            };
            
            /**
             * Refresh table lists ands status on screen
             */
            $scope.loadTableData = function() {
                return tabsResource.tableNumbers().$promise.then(function (result) {
                    $scope.tableNumbers = result;
                    if ($scope.tableNumber) {
                        return tableResource.status({'tableNumber': $scope.tableNumber}).$promise;
                    } else {
                        return null;
                    }
                }).then(function (result) {
                    $scope.tableStatus = result;
                });
            };
            
            /**
             * Aquire amount paid and sends close command
             */
            $scope.close = function() {
                tableResource.invoice({'tableNumber': $scope.tableNumber}).$promise
                        .then(function (invoiceData) {
                            var amountPaid = $window.parseInt($window.prompt('Amount paid (Tab total: $'+invoiceData.total+')'));
                            if (amountPaid) {
                                tabsResource.close({'id': $scope.tableStatus.tabId, 'amountPaid': amountPaid})
                                    .$promise.then($scope.commandFeedbackHandler(function () {
                                            $scope.tableNumber = null;
                                            $window.alert('Tab closed');
                                        })
                                    );
                            }
                        });
            };
            
            /**
             * Returns a function to handle commands feedback
             */
            $scope.commandFeedbackHandler = function (successCallback) {
                return function (status) {
                    if (status.succeded) {
                        successCallback();
                    } else {
                        $window.alert('Operation failed: '+ status.message);
                    }
                };
            };
            
            // Loads static menu and staff data
            staffResource.get().$promise.then(function (result) {
                $scope.staff = result;
                return menuResource.get().$promise;
            }).then(function (result) {
                $scope.menu = result;
            });
            
            // Register listener to tableNumber select
            $scope.$watch('tableNumber', function (newValue, oldValue){
                return $scope.loadTableData();
            });
        }]
    );
    
})(angular);


