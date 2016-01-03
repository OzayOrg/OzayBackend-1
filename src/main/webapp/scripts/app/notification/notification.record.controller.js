'use strict';

angular.module('ozayApp')
    .controller('NotificationRecordController', function($scope, $state, $stateParams, NotificationRecord, Page, UserInformation) {
        $scope.contentTitle = 'Notification Archive';

        // pagination

        $scope.setPage = function(pageNo) {
            $scope.currentPage = pageNo;
        };

        if($stateParams.pageId !== undefined){
            $scope.currentPage = 1;
        } else {
            $scope.currentPage = $stateParams.pageId;
        }

        $scope.pageChanged = function() {
            $state.go('notification-record', {pageId:$scope.currentPage});
        };

        $scope.maxSize = 8;

        Page.get({
            state: $state.current.name,
            page:$stateParams.pageId
        }).$promise.then(function(data) {
            $scope.totalItems = data.totalNumOfPages/2;
            $scope.notifications = data.notificationRecords;
            $scope.currentPage = $stateParams.pageId;
        });

    });
