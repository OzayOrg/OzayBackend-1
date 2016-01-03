'use strict';

angular.module('ozayApp')
    .factory('authInterceptor', function ($rootScope, $q, $location, $stateParams, localStorageService, $cookies, $injector) {
            return {
                // Add authorization token to headers
                request: function (config) {
                    config.headers = config.headers || {};
                    var token = localStorageService.get('token');

                    if (token && token.expires_at && token.expires_at > new Date().getTime()) {
                        config.headers.Authorization = 'Bearer ' + token.access_token;
                    }
                    var str = config.url;

                    if(str.indexOf('.html') == -1){

                        var userInformation = $injector.get('UserInformation');
                        if(userInformation.getBuilding() !== undefined){
                            config.url+=config.url.indexOf('?') === -1 ? '?' : '&'
                            config.url += 'building=' + userInformation.getBuilding().id;
                        }
                        var state  = $injector.get('$state');

                        if(state.current.parent !== undefined && state.current.parent != 'manage'){
                        }
                        else {
                             if($stateParams.organizationId !== undefined){
                                config.url+=config.url.indexOf('?') === -1 ? '?' : '&'
                                config.url += 'organization=' + $stateParams.organizationId;
                             } else if(userInformation.getOrganizationId() !== undefined && userInformation.getOrganizationId() != null){
                                 config.url+=config.url.indexOf('?') === -1 ? '?' : '&'
                                 config.url += 'organization=' + userInformation.getOrganizationId();
                             }
                        }


                    }

                    return config;
                }
            };
        })

    .factory('authExpiredInterceptor', function ($rootScope, $q, $injector, localStorageService) {
        return {
            responseError: function(response) {
                // If we have an unauthorized request we redirect to the login page
                // Don't do this check on the account API to avoid infinite loop
                if (response.status == 401 && response.data.path !== undefined && response.data.path.indexOf("/api/account") == -1){
                    var Auth = $injector.get('Auth');
                    var $state = $injector.get('$state');
                    var to = $rootScope.toState;
                    var params = $rootScope.toStateParams;
                    Auth.logout();
                    $rootScope.previousStateName = to;
                    $rootScope.previousStateNameParams = params;
                    $state.go('login');
                } else if (response.status == 403 && response.config.method != 'GET' && getCSRF() == '') {
                    // If the CSRF token expired, then try to get a new CSRF token and retry the old request
                    var $http = $injector.get('$http');
                    return $http.get('/').finally(function() { return afterCSRFRenewed(response); });
                }
                return $q.reject(response);
            }
        };

        function afterCSRFRenewed(oldResponse) {
            if (getCSRF() !== '') {
                // retry the old request after the new CSRF-TOKEN is obtained
                var $http = $injector.get('$http');
                return $http(oldResponse.config);
            } else {
                // unlikely get here but reject with the old response any way and avoid infinite loop
                return $q.reject(oldResponse);
            }
        }

        function getCSRF() {
            var name = 'CSRF-TOKEN=';
            var ca = document.cookie.split(';');
            for (var i = 0; i < ca.length; i++) {
                var c = ca[i];
                while (c.charAt(0) == ' ') c = c.substring(1);
                if (c.indexOf(name) != -1) return c.substring(name.length, c.length);
            }
            return '';
        }
    });
