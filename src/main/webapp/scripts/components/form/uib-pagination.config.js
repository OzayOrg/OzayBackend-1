'use strict';

angular.module('ozayApp')
    .config(function (uibPaginationConfig) {
        uibPaginationConfig.itemsPerPage = 20;
        uibPaginationConfig.maxSize = 5;
        uibPaginationConfig.boundaryLinks = true;
        uibPaginationConfig.firstText = '«';
        uibPaginationConfig.previousText = '‹';
        uibPaginationConfig.nextText = '›';
        uibPaginationConfig.lastText = '»';
    });
