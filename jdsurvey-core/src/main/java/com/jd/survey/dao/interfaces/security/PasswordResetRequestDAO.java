package com.jd.survey.dao.interfaces.security;

import com.jd.survey.domain.security.PasswordResetRequest;
import java.lang.Long;
import java.util.Set;
import org.skyway.spring.util.dao.JpaDao;
import org.springframework.dao.DataAccessException;

/**
 */
public interface PasswordResetRequestDAO extends JpaDao<PasswordResetRequest> {
	public Set<PasswordResetRequest> findAll() throws DataAccessException;
	public Set<PasswordResetRequest> findAll(int startResult, int maxRows) throws DataAccessException;
	public PasswordResetRequest findById(Long id) throws DataAccessException;
	public Long getCount() throws DataAccessException;
	public PasswordResetRequest findByHash(String hash) throws DataAccessException;
	
}