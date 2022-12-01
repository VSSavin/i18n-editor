package com.jvms.i18neditor.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * This class extends {@link Properties}.
 * 
 * <p>This implementation adds the ability to load and store properties from a given file path  
 * and adds support to retrieve and store {@code Integer} and {@code List} values.</p>
 * 
 * <p>This implementation also adds extended functionality like {@link #containsKeys(String...)}.</p>
 * 
 * @author Jacob van Mourik
 */
public class ExtendedProperties extends Properties {
	private final static long serialVersionUID = 6042931434040718478L;
	private final static Logger log = LoggerFactory.getLogger(ExtendedProperties.class);
	private final String listSeparator;
	private boolean useUtf8Encoding = false;

	/**
     * Creates an empty property list with no default values and "," as list separator.
     */
	public ExtendedProperties() {
		this(null, ",");
	}

	public ExtendedProperties(boolean useUtf8Encoding) {
		this();
		this.useUtf8Encoding = useUtf8Encoding;
	}
	
	/**
     * Creates an empty property list with no default values and the specified list separator.
     * 
     * @param	separator the separator used for storing list values.
     */
	public ExtendedProperties(String separator) {
		this(null, separator);
	}
	
	/**
     * Creates an empty property list with the specified defaults and "," as list separator.
     * 
     * @param	defaults the defaults.
     */
    public ExtendedProperties(Properties defaults) {
        this(defaults, ",");
    }
	
	/**
     * Creates an empty property list with the specified defaults and list separator.
     * 
     * @param	defaults the defaults.
     * @param	separator the separator used for storing list values.
     */
    public ExtendedProperties(Properties defaults, String separator) {
        super(defaults);
        this.listSeparator = separator;
    }
	
	/**
	 * Reads a property list from the given file path.
	 * 
	 * <p>Any {@code IOException} will be ignored.</p>
	 * 
	 * @param 	path the path to the property file.
	 */
	public void load(Path path) {
		if (useUtf8Encoding) {
			try(Reader reader = new InputStreamReader(new FileInputStream(path.toString()), StandardCharsets.UTF_8)) {
				load(reader);
				return;
			} catch (IOException e) {
				log.error("Unable to load properties from " + path, e);
			}
		}

		try (InputStream in = Files.newInputStream(path)) {
			load(in);
		} catch (IOException e) {
			log.error("Unable to load properties from " + path, e);
		}
	}
	
	/**
	 * Writes the property list to the given file path.
	 * 
	 * <p>Any {@code IOException} will be ignored.</p>
	 * 
	 * @param 	path the path to the property file.
	 */
	public void store(Path path) {
		if (useUtf8Encoding) {
			try (Writer writer = new OutputStreamWriter(new FileOutputStream(path.toString()), StandardCharsets.UTF_8)) {
				store(writer, null);
				return;
			} catch (IOException e) {
				log.error("Unable to store properties to " + path, e);
			}
		}
		try (OutputStream out = new OutputStreamWrapper(Files.newOutputStream(path))) {
			store(out, null);
		} catch (IOException e) {
			log.error("Unable to store properties to " + path, e);
		}
	}
	
	/**
	 * Sets a value in the property list. The list of values will be converted
	 * to a single string separated by {@value #listSeparator}.
	 * 
	 * @param 	key the key to be placed in this property list.
	 * @param 	values the value corresponding to {@code key}.
	 */
	public void setProperty(String key, List<String> values) {
		setProperty(key, values.stream().collect(Collectors.joining(listSeparator)));
	}
	
	/**
	 * Sets a value in the property list. The {@code Integer} value will be 
	 * stored as a {@code String} value.
	 * 
	 * @param 	key the key to be placed in this property list.
	 * @param 	value the value corresponding to {@code key}.
	 */
	public void setProperty(String key, Integer value) {
		setProperty(key, value == null ? null : value.toString());
	}
	
	/**
	 * Sets a value in the property list. The {@code boolean} value will be 
	 * stored as a {@code String} value; "1" for {@code true}, "0" for {@code false}.
	 * 
	 * @param 	key the key to be placed in this property list.
	 * @param 	value the value corresponding to {@code key}.
	 */
	public void setProperty(String key, Boolean value) {
		setProperty(key, value == null ? null : (value ? 1 : 0));
	}
	
	/**
	 * Sets a value in the property list. The {@code Enum} value will be 
	 * stored as a {@code String} value.
	 * 
	 * @param 	key the key to be placed in this property list.
	 * @param 	value the value corresponding to {@code key}.
	 */
	public void setProperty(String key, Enum<?> value) {
		setProperty(key, value.toString());
	}
	
	/**
	 * Sets a value in the property list. The {@code Locale} value will be 
	 * stored as a {@code String} value.
	 * 
	 * @param 	key the key to be placed in this property list.
	 * @param 	value the value corresponding to {@code key}.
	 */
	public void setProperty(String key, Locale locale) {
		setProperty(key, locale.toString());
	}
	
	/**
	 * Gets a value from the property list as a {@code List}. This method should be used
	 * to retrieve a value previously stored by {@link #setProperty(String, List)}.
	 * 
	 * @param 	key the property key.
	 * @return 	the value in this property list with the specified key value or an empty list
	 * 			if no such key exists.
	 */
	public List<String> getListProperty(String key) {
		String value = getProperty(key);
		return value == null ? Lists.newLinkedList() : Lists.newLinkedList(Arrays.asList(value.split(listSeparator)));
	}
	
	/**
	 * Gets a value from the property list as an {@code Integer}. This method should be used 
	 * to retrieve a value previously stored by {@link #setProperty(String, Integer)}.
	 * 
	 * @param 	key the property key.
	 * @return 	the value in this property list with the specified key value or {@code null}
	 * 			if no such key exists or the value is not a valid {@code Integer}.
	 */
	public Integer getIntegerProperty(String key) {
		String value = getProperty(key);
		if (!Strings.isNullOrEmpty(value)) {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
				log.warn("Unable to parse integer property value " + value);
			}
		}
		return null;
	}
	
	/**
	 * See {@link #getIntegerProperty(String)}. This method returns {@code defaultValue} when
	 * there is no value in the property list with the specified {@code key} or when 
	 * the value is not a valid {@code Integer}.
	 * 
	 * @param	key the property key.
	 * @param	defaultValue the default value to return when there is no value for the specified key
	 * @return 	the value in this property list with the specified key value or {@code defaultValue}
	 * 			if no such key exists or the value is not a valid {@code Integer}.
	 */
	public Integer getIntegerProperty(String key, Integer defaultValue) {
		Integer value = getIntegerProperty(key); 
		return value != null ? value : defaultValue;
	}
	
	/**
	 * Gets a value from the property list as an {@code Boolean}. This method should be used 
	 * to retrieve a value previously stored by {@link #setProperty(String, Boolean)}.
	 * 
	 * @param 	key the property key.
	 * @return 	the value in this property list with the specified key value or {@code null}
	 * 			if no such key exists.
	 */
	public Boolean getBooleanProperty(String key) {
		Integer value = getIntegerProperty(key);
		return value != null ? (value != 0) : null;
	}
	
	/**
	 * See {@link #getBooleanProperty(String)}. This method returns {@code defaultValue} when
	 * there is no value in the property list with the specified {@code key}.
	 * 
	 * @param 	key the property key.
	 * @param	defaultValue the default value to return when there is no value for the specified key
	 * @return 	the value in this property list with the specified key value or {@code defaultValue}
	 * 			if no such key exists.
	 */
	public Boolean getBooleanProperty(String key, boolean defaultValue) {
		Boolean value = getBooleanProperty(key);
		return value != null ? value : defaultValue;
	}
	
	/**
	 * Gets a value from the property list as an {@code Enum}. This method should be used 
	 * to retrieve a value previously stored by {@link #setProperty(String, Enum)}.
	 * 
	 * @param 	key the property key.
	 * @param	defaultValue the default value to return when there is no value for the specified key
	 * @return 	the value in this property list with the specified key value or {@code null}
	 * 			if no such key exists or the value is not a valid enum value.
	 */
	public <T extends Enum<T>> T getEnumProperty(String key, Class<T> enumType) {
		String value = getProperty(key);
		if (!Strings.isNullOrEmpty(value)) {
			try {
				return T.valueOf(enumType, value);
			} catch (Exception e) {
				log.warn("Unable to parse enum property value " + value);
			}
		}
		return null;
	}
	
	/**
	 * See {@link #getEnumProperty(String, Class)}. This method returns {@code defaultValue} when
	 * there is no value in the property list with the specified {@code key} or when 
	 * the value is not a valid enum value.
	 * 
	 * @param 	key the property key.
	 * @param	defaultValue the default value to return when there is no value for the specified key
	 * @return 	the value in this property list with the specified key value or {@code defaultValue}
	 * 			if no such key exists or the value is not a valid enum value.
	 */
	public <T extends Enum<T>> T getEnumProperty(String key, Class<T> enumType, T defaultValue) {
		T value = getEnumProperty(key, enumType);
		return value != null ? value : defaultValue;
	}
	
	/**
	 * Gets a value from the property list as an {@code Locale}. This method should be used 
	 * to retrieve a value previously stored by {@link #setProperty(String, Locale)}.
	 * 
	 * @param 	key the property key.
	 * @return 	the value in this property list with the specified key value or {@code null}
	 * 			if no such key exists.
	 */
	public Locale getLocaleProperty(String key) {
		String value = getProperty(key);
		return Locales.parseLocale(value);
	}
	
	/**
	 * See {@link #getLocaleProperty(Locale)}. This method returns {@code defaultValue} when
	 * there is no value in the property list with the specified {@code key}.
	 * 
	 * @param 	key the property key.
	 * @param	defaultValue the default value to return when there is no value for the specified key
	 * @return 	the value in this property list with the specified key value or {@code defaultValue}
	 * 			if no such key exists.
	 */
	public Locale getLocaleProperty(String key, Locale defaultValue) {
		Locale value = getLocaleProperty(key);
		return value != null ? value : defaultValue;
	}
	
	/**
	 * This function does the same as {@link #containsKey(Object)}, only for multiple keys.
	 * 
	 * @param 	keys possible keys.
	 * @return 	{@code true} if and only if the specified objects are keys in this hashtable, 
	 * 			as determined by the equals method; false otherwise.
	 */
	public boolean containsKeys(String... keys) {
		return Arrays.asList(keys).stream().allMatch(k -> containsKey(k));
	}
	
	@Override
    public synchronized Enumeration<Object> keys() {
        return Collections.enumeration(new TreeSet<Object>(super.keySet()));
    }

	@Override
	public Set<Map.Entry<Object, Object>> entrySet() {
		Set<Map.Entry<Object, Object>> parentEntrySet = super.entrySet();
		Set<Map.Entry<Object, Object>> sortedSet = new TreeSet<>(Comparator.comparing(o -> o.getKey().toString()));
		parentEntrySet.forEach(entry -> sortedSet.add(new Map.Entry<Object, Object>() {
			@Override
			public Object getKey() {
				return entry.getKey();
			}

			@Override
			public Object getValue() {
				return entry.getValue();
			}

			@Override
			public Object setValue(Object value) {
				return entry.setValue(value);
			}
		}));
		return Collections.synchronizedSet(new EntrySet(sortedSet));
	}

	private static class EntrySet implements Set<Map.Entry<Object, Object>>, Comparable<Set<Map.Entry<Object, Object>>> {
		private Set<Map.Entry<Object,Object>> entrySet;

		private EntrySet(Set<Map.Entry<Object, Object>> entrySet) {
			this.entrySet = entrySet;
		}

		@Override public int size() { return entrySet.size(); }
		@Override public boolean isEmpty() { return entrySet.isEmpty(); }
		@Override public boolean contains(Object o) { return entrySet.contains(o); }
		@Override public Object[] toArray() { return entrySet.toArray(); }
		@Override public <T> T[] toArray(T[] a) { return entrySet.toArray(a); }
		@Override public void clear() { entrySet.clear(); }
		@Override public boolean remove(Object o) { return entrySet.remove(o); }

		@Override
		public boolean add(Map.Entry<Object, Object> e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<? extends Map.Entry<Object, Object>> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return entrySet.containsAll(c);
		}

		@Override
		public boolean equals(Object o) {
			return o == this || entrySet.equals(o);
		}

		@Override
		public int hashCode() {
			return entrySet.hashCode();
		}

		@Override
		public String toString() {
			return entrySet.toString();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return entrySet.removeAll(c);
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return entrySet.retainAll(c);
		}

		@Override
		public Iterator<Map.Entry<Object, Object>> iterator() {
			return entrySet.iterator();
		}

		@Override
		public int compareTo(Set<Map.Entry<Object, Object>> o) {
			//o.forEach(objectObjectEntry -> objectObjectEntry.);
			return 0;
		}
	}
	
	private class OutputStreamWrapper extends FilterOutputStream {
        private boolean firstlineseen = false;
        
        public OutputStreamWrapper(OutputStream out) {
            super(out);
        }
        
        @Override
        public void write(int b) throws IOException {
            if (firstlineseen) {
                super.write(b);
            } else if (b == '\n') {
                firstlineseen = true;
            }
        }
    }
}