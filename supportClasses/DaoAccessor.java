package ai.expert.persistence.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DaoAccessor
{
	@Autowired
	public PersistenceEntityDao persistenceEntityDao;
}
