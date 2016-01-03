'use strict';

angular.module('ozayApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


