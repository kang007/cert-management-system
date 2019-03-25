package com.dreamsecurity.ca.business.user.service;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dreamsecurity.ca.business.common.CommonConstants;
import com.dreamsecurity.ca.business.user.dao.UserDao;
import com.dreamsecurity.ca.business.user.vo.AppliedUserInfoVo;
import com.dreamsecurity.ca.business.user.vo.UserVo;
import com.dreamsecurity.ca.framework.utils.CaUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserService {
	
	@Resource
	private UserDao userDao;
	
	private ObjectMapper objectMapper;
	
	@Autowired
	private UserService( ObjectMapper objectMapper ) {
		this.objectMapper = objectMapper;
	}
	
	public void registerAppliedUser( HttpServletRequest request ) throws JsonParseException, JsonMappingException, IOException, NoSuchAlgorithmException {
		ObjectMapper mapper = new ObjectMapper();
		AppliedUserInfoVo vo = mapper.readValue( request.getReader(), AppliedUserInfoVo.class );
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		
		
		
		vo.setAddDate( new Date() );
		vo.setState( 0 );
		vo.setPassword( CaUtils.convertByteArrayToHexString( messageDigest.digest( vo.getPassword().getBytes() ) ) );

		userDao.insertAplliedUser( vo );
	}
	
	public void rejectAppliedUser( HttpServletRequest request ) throws JsonParseException, JsonMappingException, IOException {
		AppliedUserInfoVo vo = objectMapper.readValue( request.getReader(), AppliedUserInfoVo.class );
		
		vo.setSeqId( vo.getSeqId() ); 
		vo.setState( 0 );
		
		userDao.updateAppliedUserState( vo );
	}
	
	public UserVo selectOneUser( UserVo vo ) {
		return userDao.selectOneUser( vo );
	}
	
	public List<Map<String, Object>> showList( HttpServletRequest request ) {
		List<Map<String, Object>> voMapList = userDao.selectUserList();
		
		for ( Map<String, Object> voMap : voMapList ) {
			voMap.put( "adddate", CommonConstants.dateFormat.format( (Date)voMap.get( "adddate" ) ) );
		}
		
		return voMapList;
	}
	
	public boolean chkOverlapUser( String userId ) {
		UserVo vo = new UserVo();
		
		vo.setId( userId );
		
		if ( userDao.selectUserOne4ChkOverlap( vo ) != null ) return false;
		if ( userDao.selectAppliedUserOne4ChkOverlap( vo ) != null ) return false;
		
		return true;
	}
}
