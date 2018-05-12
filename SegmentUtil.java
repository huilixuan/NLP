
public class SegmentUtil {

	/**
	 * 获得ansj第三方分词结果
	 * @param str
	 * @return
	 */
	public static Map<Integer,String> splitWord(String str){
		Map<Integer,String> map = new HashMap<Integer,String>();
		if(str==null)return map;
		AnsjTokenizer tokenizer = new AnsjTokenizer(new StringReader(str), 0, true);
		CharTermAttribute termAtt = tokenizer.addAttribute(CharTermAttribute.class);
		OffsetAttribute offsetAtt = 
				tokenizer.addAttribute(OffsetAttribute.class);
		
		try {
			while (tokenizer.incrementToken()){
				String s = termAtt.toString();
				//目前找相似的中文,英文数字先不考虑
				if(TextCharUtil.extractChinese(s).length()>0){
					Integer index = offsetAtt.startOffset(); 
					if(map.containsKey(index)){
						String v=map.get(index);
						//存储最小长度的词
						if(v.length()<s.length()){
							map.replace(index, v);
						}
					}else{
						map.put(index,  s);						
					}
				}
			}
			tokenizer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 比较两个map中的相似字符串并返回
	 * @param basemap 存储结果的map
	 * @param countmap 存储结果的记数map
	 * @param map 比较map
	 * @param fulltext 比较map的全文
	 */
	public static void compareWord(Map<Integer, String> basemap,Map<Integer, Integer> countmap, Map<Integer, String> map, String fulltext){
		if(basemap.isEmpty()){
			Iterator<Map.Entry<Integer,String>> it=map.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<Integer,String> entry = it.next();
				basemap.put(entry.getKey(), entry.getValue());
			}
		}else{
			
			List<Map.Entry<Integer,String>> list= new ArrayList<Map.Entry<Integer,String>>(basemap.entrySet());
			for(int i=0;i<list.size();i++){
				Map.Entry<Integer,String> entry = list.get(i);
				int index = entry.getKey();
				String val = entry.getValue();
				String[] nextvalarr = getNextWord(index+val.length(),basemap); 
				String nextval = nextvalarr==null?null:nextvalarr[1];
				String temp = fulltext;
				//找到相同的词,比较紧接着之后的内容是否相同,如果相同就合并这两个词
				while(temp.contains(val)){
					int ind = temp.indexOf(val);
					if(map.containsKey(new Integer(ind))){
						String samew = map.get(new Integer(ind));
						//开始比较并更新,首先取出下一个词
						String[] nextwarr =getNextWord(ind+samew.length(),map); 
						String nextw = nextwarr==null?null:nextwarr[1]; 
						//如果相同词的下一个词也相同,则更新basemap,同时countmap中index对应记数加1
						if(nextval!=null&&nextw!=null&&nextval.equals(nextw)){
							i++;
							//当前词记数加1
							if(countmap.containsKey(new Integer(index))){
								int count = countmap.get(new Integer(index))+1;
								countmap.replace(new Integer(index), count);
							}else{
								countmap.put(new Integer(index), 1);
							}
							//由于之后的词也相同,之后的词记数加1
							if(countmap.containsKey(new Integer(nextvalarr[0]))){
								int count = countmap.get(new Integer(nextvalarr[0]))+1;
								countmap.replace(new Integer(nextvalarr[0]), count);
							}else{
								countmap.put(new Integer(nextvalarr[0]), 1);
							}
							break;
						}
					}
					temp = temp.substring(ind+val.length());
				}
			}
		}
		
	}
	/**
	 * 找到对应的词之后的词,之后从某索引号开始找
	 * @param index 开始找的索引号
	 * @param word 索引号对应的词
	 * @param map 分词map
	 * @return
	 */
	public static String[] getNextWord(int index, Map<Integer, String> map){
		for(int i=index;i<=index+10;i++){
			if(map.containsKey(i)){
				return new String[]{i+"",map.get(i)};
			}
		}
		return null;
	}
	
	/**
	 * 把词频分析结果存入数据库
	 * @param list
	 */
public static void saveWordcount(List<TbWordcount> list){
	ICommon<TbWordcount> ic = new CommonImp<TbWordcount>(TbWordcount.class);
	for(TbWordcount w:list){
		TbWordcount en = ic.findByProperty("word", w.getWord());
		try{
			if(en!=null){
				w.setId(en.getId());
				w.setWordcount(w.getWordcount()>en.getWordcount()?w.getWordcount():en.getWordcount());
				EntityManagerHelper.beginTransaction();
				ic.delete(en.getId());
				EntityManagerHelper.commit();
			}else{
				en = ic.findLastRecord("", "id", "desc");
				w.setId(en==null?1:(en.getId()+1));
			}
			EntityManagerHelper.beginTransaction();
			System.out.println("w2:"+w.getWord());
			ic.save(w);
			EntityManagerHelper.commit();
			EntityManagerHelper.closeEntityManager();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
/*
 //合并相邻的词
		for(int i=0;i<resultnew.size()-1;i++){
			if(resultnew.get(i)!=null)
			//从该词的位置之后开始判断,是否相邻,相邻就合并,把被合并的置为null
			for(int j=i+1;j<resultnew.size();j++){
				Object[] obj = resultnew.get(i);
				int firstindex = (int)obj[0];
				String firstword=(String)obj[1];
				int firstcount = (int)obj[2];
				Object[] nextobj = resultnew.get(j);
				//判断
				int nextindex = (int)nextobj[0];
				String nextword = (String)nextobj[1];
				int nextcount = (int)nextobj[2];
				if(nextindex-firstindex==firstword.length()){
					firstcount = firstcount<nextcount?firstcount:nextcount;
					resultnew.set(i, new Object[]{firstindex, firstword+nextword, firstcount});
					resultnew.set(j, null);
				}else{
					break;
				}
			}
		}
  */

public static void main(String[] args){
	Wordcount w = new Wordcount();
	w.setWord("智联简历");
	
	ICommon<Wordcount> ic = new CommonImp<Wordcount>(Wordcount.class);
	EntityManagerHelper.beginTransaction();
	System.out.println("w2:"+w.getWord());
	ic.save(w);
	EntityManagerHelper.commit();
	EntityManagerHelper.closeEntityManager();
}
}
