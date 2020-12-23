-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        10.4.8-MariaDB - mariadb.org binary distribution
-- 서버 OS:                        Win64
-- HeidiSQL 버전:                  10.2.0.5599
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- music 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `music` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `music`;

-- 테이블 music.artist 구조 내보내기
CREATE TABLE IF NOT EXISTS `artist` (
  `aid` int(11) unsigned NOT NULL,
  `rrn` varchar(13) NOT NULL DEFAULT '',
  `agency` varchar(50) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`aid`),
  UNIQUE KEY `rrn` (`rrn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 테이블 데이터 music.artist:~4 rows (대략적) 내보내기
/*!40000 ALTER TABLE `artist` DISABLE KEYS */;
INSERT INTO `artist` (`aid`, `rrn`, `agency`, `name`) VALUES
	(24, '2424242424242', 'sms', 'msg'),
	(55, '5555555555555', 'roen', 'iu'),
	(365, '0000000000000', 'sm', 'taeyeon'),
	(1004, '3333333333333', 'sm', 'gyuhyun');
/*!40000 ALTER TABLE `artist` ENABLE KEYS */;

-- 테이블 music.manager 구조 내보내기
CREATE TABLE IF NOT EXISTS `manager` (
  `mid` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `rrn` varchar(13) NOT NULL,
  `name` varchar(50) NOT NULL,
  `phone` varchar(11) DEFAULT NULL,
  PRIMARY KEY (`mid`),
  UNIQUE KEY `rrn` (`rrn`)
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8;

-- 테이블 데이터 music.manager:~2 rows (대략적) 내보내기
/*!40000 ALTER TABLE `manager` DISABLE KEYS */;
INSERT INTO `manager` (`mid`, `rrn`, `name`, `phone`) VALUES
	(22, '2222222222222', 'bogum', '01000000000'),
	(111, '1111111111111', 'chanwi', '01012345678');
/*!40000 ALTER TABLE `manager` ENABLE KEYS */;

-- 테이블 music.music 구조 내보내기
CREATE TABLE IF NOT EXISTS `music` (
  `muid` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `mid` int(11) unsigned zerofill DEFAULT NULL,
  `date` date DEFAULT NULL,
  `title` varchar(13) NOT NULL,
  PRIMARY KEY (`muid`),
  KEY `FK_music_manager` (`mid`)
) ENGINE=InnoDB AUTO_INCREMENT=988 DEFAULT CHARSET=utf8;

-- 테이블 데이터 music.music:~8 rows (대략적) 내보내기
/*!40000 ALTER TABLE `music` DISABLE KEYS */;
INSERT INTO `music` (`muid`, `mid`, `date`, `title`) VALUES
	(10, 00000000111, '2019-12-04', 'happy'),
	(23, 00000000022, '2019-12-04', 'you and i'),
	(33, 00000000022, '2019-12-04', 'hungry'),
	(99, NULL, NULL, 'im so tired'),
	(444, 00000000022, '2019-12-04', 'hello'),
	(666, 00000000111, '2019-12-04', 'byehello'),
	(777, 00000000111, '2019-12-04', 'lucky'),
	(987, NULL, NULL, 'helloworld');
/*!40000 ALTER TABLE `music` ENABLE KEYS */;

-- 테이블 music.musiclist 구조 내보내기
CREATE TABLE IF NOT EXISTS `musiclist` (
  `uid` int(11) unsigned NOT NULL,
  `pname` varchar(10) NOT NULL DEFAULT '',
  `muid` int(11) unsigned NOT NULL,
  PRIMARY KEY (`uid`,`pname`,`muid`),
  KEY `FK_add_music` (`muid`),
  CONSTRAINT `FK_add_music` FOREIGN KEY (`muid`) REFERENCES `music` (`muid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_add_playlist` FOREIGN KEY (`uid`, `pname`) REFERENCES `playlist` (`uid`, `pname`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 테이블 데이터 music.musiclist:~5 rows (대략적) 내보내기
/*!40000 ALTER TABLE `musiclist` DISABLE KEYS */;
INSERT INTO `musiclist` (`uid`, `pname`, `muid`) VALUES
	(1, 'dance', 10),
	(1, 'dance', 444),
	(1, 'dance', 666),
	(1, 'pop', 33),
	(1, 'pop', 666);
/*!40000 ALTER TABLE `musiclist` ENABLE KEYS */;

-- 테이블 music.playlist 구조 내보내기
CREATE TABLE IF NOT EXISTS `playlist` (
  `uid` int(11) unsigned NOT NULL,
  `pname` varchar(10) NOT NULL,
  `date` date DEFAULT NULL,
  `count` int(10) unsigned DEFAULT 0,
  PRIMARY KEY (`uid`,`pname`),
  CONSTRAINT `FK_uid` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 테이블 데이터 music.playlist:~3 rows (대략적) 내보내기
/*!40000 ALTER TABLE `playlist` DISABLE KEYS */;
INSERT INTO `playlist` (`uid`, `pname`, `date`, `count`) VALUES
	(1, 'dance', '2019-12-04', 3),
	(1, 'pop', '2019-12-08', 2);
/*!40000 ALTER TABLE `playlist` ENABLE KEYS */;

-- 테이블 music.released 구조 내보내기
CREATE TABLE IF NOT EXISTS `released` (
  `aid` int(11) unsigned NOT NULL,
  `muid` int(11) unsigned NOT NULL,
  `date` date DEFAULT NULL,
  PRIMARY KEY (`muid`,`aid`),
  KEY `FK_release_artist` (`aid`),
  CONSTRAINT `FK_release_artist` FOREIGN KEY (`aid`) REFERENCES `artist` (`aid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_release_music` FOREIGN KEY (`muid`) REFERENCES `music` (`muid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 테이블 데이터 music.released:~9 rows (대략적) 내보내기
/*!40000 ALTER TABLE `released` DISABLE KEYS */;
INSERT INTO `released` (`aid`, `muid`, `date`) VALUES
	(365, 10, '2019-12-04'),
	(365, 23, '2019-12-04'),
	(55, 33, '2019-12-04'),
	(55, 99, '2019-12-04'),
	(55, 444, '2019-12-04'),
	(1004, 444, '2019-12-04'),
	(55, 666, '2019-12-04'),
	(24, 777, '2019-12-04'),
	(1004, 987, '2019-12-04');
/*!40000 ALTER TABLE `released` ENABLE KEYS */;

-- 테이블 music.user 구조 내보내기
CREATE TABLE IF NOT EXISTS `user` (
  `uid` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `rrn` varchar(13) NOT NULL,
  `name` varchar(50) NOT NULL,
  `phone` varchar(11) DEFAULT '',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `rrn` (`rrn`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- 테이블 데이터 music.user:~3 rows (대략적) 내보내기
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` (`uid`, `rrn`, `name`, `phone`) VALUES
	(1, '1111111111111', 'chanwi', '01012345678'),
	(2, '2222222222222', 'bogum', '01000000000'),
	(3, '3333333333333', 'seungki', '01087654321');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
