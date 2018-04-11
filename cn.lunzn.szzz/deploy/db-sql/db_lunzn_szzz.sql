/*Table structure for table `t_audit_flow` */

CREATE TABLE `t_audit_flow` (
  `flowid` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号，自增',
  `submitstaffname` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '提交人员工姓名',
  `submitstaffid` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '提交人员工工号',
  `staffname` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '处理人员工姓名',
  `staffid` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '处理人员工工号',
  `audittype` int(11) NOT NULL COMMENT '审核类型 1-年度述职 2-员工转正',
  `auditid` int(11) NOT NULL COMMENT '审核编号,年度述职、员工转正记录编号',
  `handleflag` int(11) NOT NULL COMMENT '处理标识1-待处理 2-已处理',
  `auditstep` int(11) NOT NULL DEFAULT '1' COMMENT '审核步骤 1-部门 2-人事 3-总经理',
  `auditvalue` varchar(2048) COLLATE utf8_bin DEFAULT NULL COMMENT '审核内容，任意格式',
  `state` int(11) DEFAULT NULL COMMENT '审核状态1-通过 2-驳回',
  `begintime` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '电子流记录日期',
  `finishtime` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '电子流处理日期',
  PRIMARY KEY (`flowid`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `t_sz_info` */

CREATE TABLE `t_sz_info` (
  `szid` int(11) NOT NULL AUTO_INCREMENT COMMENT '述职编号',
  `staffid` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '处理人员工号',
  `department` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `position` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `staffname` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '员工姓名',
  `timequantum` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '述职年份',
  `entrydate` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '员工入职日期',
  `graddate` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '员工毕业日期',
  `education` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '员工学历',
  `state` int(11) DEFAULT NULL COMMENT '审核状态 1-编辑中2-待审核 3-通过 4-驳回',
  `submitdate` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '审核日期',
  `workresults` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '工作成果',
  `evaluation` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '自我评价',
  `deficiency` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '不足&改进',
  `plan` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '成长规划',
  `auditor` varchar(4096) COLLATE utf8_bin DEFAULT NULL COMMENT '审核人',
  `cc` varchar(4096) COLLATE utf8_bin DEFAULT NULL COMMENT '抄送人',
  PRIMARY KEY (`szid`),
  KEY `STAFFID_INDEX` (`staffid`)
) ENGINE=InnoDB AUTO_INCREMENT=124 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `t_zz_info` */

CREATE TABLE `t_zz_info` (
  `zzid` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键，自增长',
  `staffid` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '员工编号',
  `position` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `department` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `staffname` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '员工姓名',
  `entrydate` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '员工入职日期',
  `graddate` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '员工毕业日期',
  `education` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '员工学历',
  `zzdate` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '转正日期',
  `state` int(11) DEFAULT NULL COMMENT '转正状态 1-编辑中2-待审核 3-通过 4-驳回',
  `submitdate` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '提交日期',
  `effort` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '工作成果',
  `gain` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '个人收获',
  `suggest` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '心得&建议',
  `auditor` varchar(4096) COLLATE utf8_bin DEFAULT NULL COMMENT '审核人',
  `cc` varchar(4096) COLLATE utf8_bin DEFAULT NULL COMMENT '抄送人',
  PRIMARY KEY (`zzid`),
  UNIQUE KEY `STAFFID_INDEX` (`staffid`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
