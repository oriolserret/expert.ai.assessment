package ai.expert.persistence.dao;

import java.util.Collection;

public interface AbstractDao<T>
{
	public T findById(Long id);

	public Collection<T> findByProperty(String name, Object value);

	public T findByUniqueProperty(String name, Object value)

	public Collection<T> findAll();

	public void persist(T entity);

	public void delete(T entity);

	public int delete(Long id);

	public int deleteByProperty(String name, Object value);

	public T load(Long id);

	public void executePartialCommit();
}
