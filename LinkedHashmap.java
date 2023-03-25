package project;


import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class LinkedHashmap<K,V>  {
  
  private int size;
  private Entry<K,V>[] table;
  private int cap=16;
  private Entry<K,V> head;
  private Entry<K,V> tail;
  private Entry<K,V> current;
  static class Entry<K,V> implements Map.Entry<K, V>{
	  K key;
	  V value;
	  Entry<K,V> next=null;
	  Entry<K,V> after=null;
	  Entry<K,V> before=null;
	  Entry(K key,V value,Entry<K,V> next){
		  this.key=key;
		  this.value=value;
		  this.next=next;
	  }
	@Override
	public K getKey() {
		
		return key;
	}
	@Override
	public V getValue() {
		
		return value;
	}
	@Override
	public V setValue(V value) {
		
		return this.value=value;
	}
	public String toString() {
		return key+"="+value;
	}
  }
  
 
   public LinkedHashmap() {
	  table=new Entry[this.cap];
	  size=0;
  }
	
   public LinkedHashmap(int cap) {
	  table=new Entry[cap];
	  size=0;
  }
    public LinkedHashmap(Map <? extends K,? extends V> col) {
    	if(col==null) {
    		table=new Entry[this.cap];
    	}
    	else {
    		table=new Entry[col.size()];
    		for(Map.Entry<? extends K, ? extends V> e : col.entrySet()) {
    			K key=e.getKey();
    			V value=e.getValue();
    			put(key,value);
    		}
    	}
    	
    }
   public void put(K key,V value) {
	   if(table.length == size) {
		   cap=cap*2;
		   Entry<K,V>[] bed=new Entry[cap];
		   for(int i=0;i<table.length;i++) {
			   bed[i]=table[i];
		   }
		   table=bed;
	   }
	if(key == null) {
		throw new NullPointerException();
	}
	boolean replace = false;
	int hash=hash(key)%table.length;
	Entry<K,V> newEntry=new Entry<K,V>(key,value,null);
	current=newEntry;
	Entry<K,V> curr=table[hash];
	
	if(curr == null) {
		table[hash]=newEntry;
	}
	else {
		Entry<K,V> prev=null;

	while(curr != null) {
		if(curr.key.equals(key)) {
			replace=true;
			curr.value=value;
			break;
		}
		prev=curr;
		curr=curr.next;
	}
	if(prev != null) {
		prev.next=newEntry;
	}
	}
	if(replace == false) {
		InsertInList(newEntry);
	}
	
}

private void InsertInList(Entry<K, V> newEntry) {
	if(head == null) {
		head =newEntry;
		tail=newEntry;
		size++;
	}
	else {
		tail.after=newEntry;
		newEntry.before=tail;
		tail=newEntry;
		size++;
	}
	
	
}

   static final int hash(Object key) {
	int h;
	return (key == null)? 0:(h=key.hashCode()) ^ (h >>> 16);
}

    public String toString() {
	
	StringBuilder bui=new StringBuilder("[");
	Entry<K,V>curr=head;
	if(curr== null) {
		return (bui.toString()+"]");
	}
	else{
		while(curr != null) {
		bui.append(curr.key+"="+curr.value+",");
		curr=curr.after;
	}
	}
	return bui.substring(0,bui.length()-1)+"]";
  }
    
    public V get(Object key) {
    	int hash=hash(key)%table.length;
    	Entry<K,V> curr=table[hash];
    	while(curr != null) {
    		if(curr.key.equals(key)) {
    			return curr.value;
    		}
    		curr=curr.next;
    	}
    	return null;
    }
    
    public V remove(K key) {
    	V value=get(key);
    	int hash=hash(key)%table.length;
    	Entry<K,V> curr=table[hash];
    	if(curr == null) {
    		return null;
    	}
    	Entry<K,V> p=null;
    	Entry<K,V> n = null;
    	
    	while(curr != null) {
    		n=curr.next;
    		if(p == null) {
    			table[hash]=table[hash].next;
    		}
    		else {
    			p.next=n;
    		}
    		adjustList(curr);
    		break;
    	}
    	p = curr;
		curr = n;
		return value;
    }

	private void adjustList(Entry<K, V> curr) {
	
		if(curr == head) {
			head=head.after;
			if(head == null) {
				tail=null;
			}
			size--;
		}
		else if(curr == tail) {
			tail=tail.before;
			tail.after=null;
			size--;
		}
		else {
			curr.before.after=curr.after;
			curr.after.before=curr.before;
			size--;
		}
	}
	public V remove() {
		if(current != null) {
			V value=remove(current.key);
			if(value != null) {
			current=current.before;
			return value;
			}
			else {
				return null;
			}
		}
		return null;
	}
	
	public int size() {
		return this.size;
	}

  final <T> T[] KeysToArray(T[] a) {
	  Object[] r=a;
	  int rdx=0;
	  for(Entry<K,V> e=head;e!=null;e=e.after) {
		  r[rdx++]=e.key;
	  }
	  return a;
  }
  final <T> T[] ValuesToArray(T[] a) {
	  Object[] r=a;
	  int rdx=0;
	  for(Entry<K,V> e=head;e!=null;e=e.after) {
		  r[rdx++]=e.value;
	  }
	  return a;
  }
  
  
  public Object[] KeysToArray() {
	return KeysToArray(new Object[size]);
  }
  
  
  public Object[] ValuesToArray() {
		return ValuesToArray(new Object[size]);
	  }
  
  public final void forEach(Consumer<? super K> action) {
	  if(action == null) {
		  throw new NullPointerException();
	  }
	  else {
		  int mc=size;
		  for(LinkedHashmap.Entry<K,V> e=head;e!=null;e=e.after) {
			  action.accept(e.key);
		  }
		  if(mc != size) {
			  throw new ConcurrentModificationException();
		  }
	  }
  }
  
  public Set<Map.Entry<K,V>> entrySet(){
	  Set<Map.Entry<K,V>> es= new LinkedHashSet<>();
	  for(LinkedHashmap.Entry<K,V> e=head;e!= null;e=e.after) {
		  es.add(e);
	  }
	  return (es == null)? new LinkedHashSet<Map.Entry<K,V>>() : es;
  }
  
  
  public final void fforEach(Consumer<? super Map.Entry<K,V>> action) {
	  if(action == null) {
		  throw new NullPointerException();
	  }
	  else {
		  int mc=size;
		  for(LinkedHashmap.Entry<K,V> e=head;e!=null;e=e.after) {
			  action.accept((java.util.Map.Entry<K, V>) e);
		  }
		  if(mc != size) {
			  throw new ConcurrentModificationException();
		  }
	  }
}
  
  public final void ForEach(Consumer<? super V> action) {
	  if(action == null) {
		  throw new NullPointerException();
	  }
	  else {
		  int mc=size;
		  for(LinkedHashmap.Entry<K,V> e=head;e!=null;e=e.after) {
			  action.accept(e.value);
		  }
		  if(mc != size) {
			  throw new ConcurrentModificationException();
		  }
	  }
  }
  
  
  public void forEach(BiConsumer<? super K,? super V> action) {
	  if(action == null) {
		  throw new NullPointerException();
	  }
	  else {
		  int mc=size;
		  for(LinkedHashmap.Entry<K,V> e=head;e!=null;e=e.after) {
			  action.accept(e.key,e.value);
		  }
		  if(mc != size) {
			  throw new ConcurrentModificationException();
		  }
	  }
  }
 
  
  public void clear() {
	  table=new Entry[this.cap];
	  size=0;
	  head=tail=null;
  }
  
  public void replaceAll(BiFunction<? super K,? super V,? extends V> function) {
	  if(function == null) {
		  throw new NullPointerException();
	  }
	  int mc=size;
	  for(LinkedHashmap.Entry<K,V> e=head;e!=null;e=e.after)
		  e.value=function.apply(e.key, e.value);
	  if(mc != size) {
		  throw new ConcurrentModificationException();
	  }
  }
  
  public Set<K> keySet(){
	  Set<K> es= new LinkedHashSet<>();
	  for(LinkedHashmap.Entry<K,V> e=head;e!= null;e=e.after) {
		  es.add(e.key);
	  }
	  return (es == null)? new LinkedHashSet<K>() : es;
  }
  
  
  public boolean containsValue(Object value) {
	  for(LinkedHashmap.Entry<K,V> e=head;e!=null;e=e.after) {
		  V v=e.value;
		  if(v==value || (value != null && value.equals(v))) {
			  return true;
		  }
	  }
	  return false;
  }
  
  public boolean containsKey(Object key) {
	  for(LinkedHashmap.Entry<K,V> e=head;e!=null;e=e.after) {
		  K k=e.key;
		  if(k==key || (key != null && key.equals(k))) {
			  return true;
		  }
	  }
	  return false;
  }
  
  public Collection<V> values(){
	  Collection<V> vs =new ArrayList<V>();
	  for(LinkedHashmap.Entry<K, V> e=head;e!=null;e=e.after) {
		  vs.add(e.value);
	  }
	  return vs;
  }
  
  public boolean equals(Object o) {
	  if(o == this) {
		  return true;
	  }
	  if(!(o instanceof LinkedHashmap<?,?> m)) 
		  return false;
	  if(m.size != size())
	     return false;
	  
	  try {
		  for(LinkedHashmap.Entry<K, V> e=head;e!=null;e=e.after) {
			  K key=e.getKey();
			  V value=e.getValue();
			  if(value == null) {
				  if(!(m.get(key) == null && m.containsKey(key))) {
					  return false;
				  }
			  }
			  else {
				  if(!value.equals(m.get(key))) {
					  return false;
				  }
			  }
		  }
	  }
	  catch(ClassCastException ex) {
		  return false;
	  }
	  catch(NullPointerException ex) {
		  return false;
	  }
	  return true;
  }
  }

