CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `username` varchar(256) DEFAULT NULL,
                        `userAccount` varchar(256) DEFAULT NULL,
                        `avatarUrl` varchar(1024) DEFAULT NULL,
                        `gender` tinyint DEFAULT NULL,
                        `userPassword` varchar(512) NOT NULL,
                        `phone` varchar(128) DEFAULT NULL,
                        `email` varchar(256) DEFAULT NULL,
                        `userStatus` int NOT NULL DEFAULT '0',
                        `createTime` datetime DEFAULT NULL,
                        `updateTime` datetime DEFAULT NULL,
                        `isDelete` tinyint DEFAULT '0',
                        `role` int NOT NULL DEFAULT '0' COMMENT '用户角色 0-普通用户，1-管理员',
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
