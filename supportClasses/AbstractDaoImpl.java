package ai.expert.persistence.dao.impl;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import ai.expert.persistence.dao.AbstractDao;

public class AbstractDaoImpl<T> implements AbstractDao<T>
{
	private static Logger LOG = LogManager.getLogger(AbstractDaoImpl.class);

	private static int DEFAULT_MAX_RESULTS = 25;

	private final Class<T> persistentClass;

	@Autowired
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	public AbstractDaoImpl()
	{
		this.persistentClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	protected Session getSession()
	{
		return this.sessionFactory.getCurrentSession();
	}

	/**
	 * <b>WARNING</b>: This method should only be used when there is no other possibility</br>
	 * Think carefully what you are doing before you use this method. The standard method should be "getSession"<br/>
	 * <b>IMPORTANT</b>: Really! Do not use this method if you don't know exactly what you are doing
	 * 
	 * @return New Database session
	 */
	protected Session openNewSession()
	{
		return this.sessionFactory.openSession();
	}

	@Override
	public T findById(Long id)
	{
		return (T) getSession().get(this.persistentClass, id);
	}

	@Override
	public T load(Long id)
	{
		if (id == null)
			return null;

		return (T) getSession().load(this.persistentClass, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<T> findByProperty(String name, Object value)
	{
		String hql;
		Query query;

		if (value instanceof Collection<?>)
			hql = "from " + this.persistentClass.getCanonicalName() + " where " + name + " in :value";
		else
			hql = "from " + this.persistentClass.getCanonicalName() + " where " + name + " = :value";
		query = getSession().createQuery(hql);
		query.setParameter("value", value);
		return (Collection<T>) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T findByUniqueProperty(String name, Object value) throws Exception
	{
		String hql;
		Query query;
		Collection<T> collection;

		hql = "from " + this.persistentClass.getCanonicalName() + " where " + name + " = :value";
		query = getSession().createQuery(hql);
		query.setParameter("value", value);
		collection = (Collection<T>) query.getResultList();
		if (collection == null)
			return null;
		if (collection.size() == 0)
			return null;
		if (collection.size() > 1)
			throw new Exception("Attribute '" + name + "' is not unique. '" + collection.size() + "' entities where found for value '" + value + "'");

		return collection.iterator().next();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<T> findAll()
	{
		return (Collection<T>) this.getSession().createQuery("from " + this.persistentClass.getCanonicalName()).getResultList();
	}

	@Override
	public void persist(T entity)
	{
		getSession().persist(entity);
	}

	@Override
	public void delete(T entity)
	{
		getSession().delete(entity);
		getSession().flush();
	}

	@Override
	public int delete(Long id)
	{
		String hql;
		Query query;

		hql = "delete from " + this.persistentClass.getCanonicalName() + " where id = :id";
		query = getSession().createQuery(hql);
		query.setParameter("id", id);
		return query.executeUpdate();
	}

	public int deleteByProperty(String name, Object value)
	{
		String hql;
		Query query;

		hql = "delete from " + this.persistentClass.getCanonicalName() + " where " + name + " = :value";
		query = getSession().createQuery(hql);
		query.setParameter("value", value);
		return query.executeUpdate();
	}

	protected void addParametersToQuery(Map<String, Object> parameters, Query query)
	{
		for (Map.Entry<String, Object> entry : parameters.entrySet())
			query.setParameter(entry.getKey(), entry.getValue());
	}

	/**
	 * @param query
	 * @param page
	 *            Zero based page index
	 * @param maxResults
	 */
	protected void setPaginationAttributes(Query query, Integer page, Integer maxResults)
	{
		if (maxResults != null)
		{
			if (maxResults >= 0)
				query.setMaxResults(maxResults);
		}
		else
			query.setMaxResults(AbstractDaoImpl.DEFAULT_MAX_RESULTS);

		if (page != null)
			query.setFirstResult(page * query.getMaxResults());
	}

	protected T getSingleResultOrNull(TypedQuery<T> query)
	{
		List<T> results;
		results = query.getResultList();

		if (results.isEmpty())
			return null;

		if (results.size() == 1)
			return results.get(0);

		throw new NonUniqueResultException();
	}

	@Override
	public void executePartialCommit()
	{
		Transaction transaction;

		AbstractDaoImpl.LOG.debug("Executing partial commit");
		this.getSession().flush();
		this.getSession().clear();
		transaction = this.getSession().getTransaction();
		transaction.commit();
		transaction.begin();
	}
}
