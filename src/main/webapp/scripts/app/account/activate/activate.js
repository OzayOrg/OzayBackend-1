'use strict';

angular.module('ozayApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('activate', {
                url: '/activate?key',
                data: {
                    authorities: [],
                    pageTitle: 'Activation'
                },
                views: {
                    'other@': {
                        templateUrl: 'scripts/app/account/activate/activate.html',
                        controller: 'ActivationController'
                    }
                },
                resolve: {

                }
            });
    });
