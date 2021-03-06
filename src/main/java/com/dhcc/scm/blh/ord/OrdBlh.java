/**
 * 通过模板生成Blh 
 * template by zxx
 */
package com.dhcc.scm.blh.ord;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import com.dhcc.framework.app.blh.AbstractBaseBlh;
import com.dhcc.framework.app.service.CommonService;
import com.dhcc.framework.common.BaseConstants;
import com.dhcc.framework.transmission.event.BusinessRequest;
import com.dhcc.framework.util.JsonUtils;
import com.dhcc.framework.util.StringUtils;
import com.dhcc.framework.web.context.WebContextHolder;
import com.dhcc.scm.blh.nur.NurseBlh;
import com.dhcc.scm.dto.ord.OrdDto;
import com.dhcc.scm.dto.sys.SysImpModelDto;
import com.dhcc.scm.entity.hop.HopCtlocDestination;
import com.dhcc.scm.entity.hop.HopInc;
import com.dhcc.scm.entity.hop.HopVendor;
import com.dhcc.scm.entity.ord.Ord;
import com.dhcc.scm.entity.ord.OrderDetail;
import com.dhcc.scm.entity.ord.OrderItm;
import com.dhcc.scm.entity.sys.ImpModel;
import com.dhcc.scm.entity.sys.SysLog;
import com.dhcc.scm.entity.userManage.NormalAccount;
import com.dhcc.scm.entity.ven.VenDeliveritm;
import com.dhcc.scm.entity.ven.VenHopInc;
import com.dhcc.scm.entity.ven.VenInc;
import com.dhcc.scm.entity.vo.ws.OperateResult;
import com.dhcc.scm.service.hop.HopCtlocDestinationService;
import com.dhcc.scm.service.hop.HopIncService;
import com.dhcc.scm.service.hop.HopVendorService;
import com.dhcc.scm.service.ord.OrdService;
import com.dhcc.scm.service.sys.SysImpModelService;
import com.dhcc.scm.service.ven.VenIncService;


@Component
public class OrdBlh extends AbstractBaseBlh {

	
	@Resource
	private NurseBlh nurseBlh;
	
	@Resource
	private OrdService ordService;
	
	@Resource
	private CommonService commonService;
	
	@Resource
	private VenIncService venIncService;
	
	@Resource
	private SysImpModelService sysImpModelService;
	
	@Resource
	private HopIncService hopIncService;
	
	@Resource
	private HopVendorService hopVendorService;
	
	@Resource
	private HopCtlocDestinationService hopCtlocDestinationService;
	
	public OrdBlh() {
		
	}
	

	public void list(BusinessRequest res) {
	
		OrdDto dto = super.getDto(OrdDto.class, res);
		
		//调用对应的service方法
		ordService.list(dto);
	}
	
	public void listItm(BusinessRequest res) throws IOException {
		
		OrdDto dto = super.getDto(OrdDto.class, res);
		
		//调用对应的service方法
		ordService.listOrdr(dto);
		WebContextHolder.getContext().getResponse().getWriter().write(JsonUtils.toJson(dto.getOrdVos()));
	}
	
	
	/**
	 * 
	* @Title: OrdBlh.java
	* @Description: TODO(用一句话描述该文件做什么)
	* @param res
	* @throws IOException
	* @return:void 
	* @author zhouxin  
	* @date 2014年8月28日 下午3:35:14
	* @version V1.0
	 */
	public void deleteItm(BusinessRequest res) throws IOException {
		
		OrdDto dto = super.getDto(OrdDto.class, res);
		dto.setOpFlg("0");
		try{
		//调用对应的service方法
		ordService.deleteItm(dto);
		dto.setOpFlg("1");
		}catch(Exception e){
			e.printStackTrace();
			dto.setMsg(e.getMessage());
		}finally{
			WebContextHolder.getContext().getResponse().getWriter().write(JsonUtils.toJson(dto));
		}
		
	}
	
	/**
	 * 
	* @Title: OrdBlh.java
	* @Description: TODO(用一句话描述该文件做什么)
	* @return:void 
	* @author zhouxin  
	* @date 2014年8月29日 上午9:34:16
	* @version V1.0
	 * @throws IOException 
	 */
	public void saveOrditm(BusinessRequest res) throws IOException{
		OrdDto dto = super.getDto(OrdDto.class, res);
		try{
			OrderItm orderItm=commonService.get(OrderItm.class,dto.getOrderItmId());
			orderItm.setReqqty(dto.getQty());
			orderItm.setRp(dto.getRp());
			orderItm.setIncId(dto.getIncId());
			orderItm.setUom(dto.getUom());
			commonService.saveOrUpdate(orderItm);
			dto.setOpFlg("1");
		}catch(Exception e){
			dto.setOpFlg("-1");
			dto.setMsg(e.getMessage());
		}finally{
			WebContextHolder.getContext().getResponse().getWriter().write(JsonUtils.toJson(dto));
		}
		
	}
	
	public void listDelV(BusinessRequest res) {
		
		OrdDto dto = super.getDto(OrdDto.class, res);
		
		//调用对应的service方法
		ordService.listDelv(dto);
	}
	
	
	/**
	 * 
	* @Title: OrdBlh.java
	* @Description: TODO(用一句话描述该文件做什么)
	* @param res
	* @return:void 
	* @author zhouxin  
	* @date 2014年8月29日 下午4:37:39
	* @version V1.0
	 * @throws IOException 
	 */
	public void listDelVItm(BusinessRequest res) throws IOException {
		
		OrdDto dto = super.getDto(OrdDto.class, res);
		
		//调用对应的service方法
		ordService.listDeliverItm(dto);
		WebContextHolder.getContext().getResponse().getWriter().write(JsonUtils.toJson(dto.getDelvVos()));
	}
	
	
	/**\
	 * 
	* @Title: OrdBlh.java
	* @Description: TODO(删除明细)
	* @param res
	* @throws IOException
	* @return:void 
	* @author zhouxin  
	* @date 2014年8月29日 下午4:54:34
	* @version V1.0
	 */
	public void deleteDelVItm(BusinessRequest res) throws IOException {
		
		OrdDto dto = super.getDto(OrdDto.class, res);
		dto.setOpFlg("0");
		try{
		//调用对应的service方法
		ordService.deleteDelvItm(dto);
		dto.setOpFlg("1");
		}catch(Exception e){
			e.printStackTrace();
			dto.setMsg(e.getMessage());
		}finally{
			WebContextHolder.getContext().getResponse().getWriter().write(JsonUtils.toJson(dto));
		}
	}
	
	public void saveDelVItm(BusinessRequest res) throws IOException {
		
		OrdDto dto = super.getDto(OrdDto.class, res);
		try{
			VenDeliveritm deliveritm=commonService.get(VenDeliveritm.class,dto.getDeliverItmid());
			deliveritm.setDeliveritmBatno(dto.getBatno());
			deliveritm.setDeliveritmExpdate(dto.getExpdate());
			deliveritm.setDeliveritmInvnoe(dto.getInvno());
			deliveritm.setDeliveritmQty(dto.getQty());
			deliveritm.setDeliveritmRp(dto.getRp());
			
			Float fac=deliveritm.getDeliveritmFac();
			if(fac==null){
				fac=venIncService.getFacByhopInc(deliveritm.getDeliveritmHopincid(), WebContextHolder.getContext().getVisit().getUserInfo().getVendorIdLong());
				deliveritm.setDeliveritmFac(fac);
			}
			if(dto.getQty()!=null){
				deliveritm.setDeliveritmHisQty(dto.getQty().floatValue()*fac);
			}
			if(dto.getRp()!=null){
				deliveritm.setDeliveritmHisRp(dto.getRp().floatValue()*fac);
			}
			commonService.saveOrUpdate(deliveritm);
			dto.setOpFlg("1");
		}catch(Exception e){
			dto.setOpFlg("-1");
			dto.setMsg(e.getMessage());
			e.printStackTrace();
		}finally{
			WebContextHolder.getContext().getResponse().getWriter().write(JsonUtils.toJson(dto));
		}
	}
	
	
	/**
	 * 
	* @Title: cmpImpOrd 
	* @Description: TODO(确认完成导入订单) 
	* @param @param res
	* @param @throws IOException    设定文件 
	* @return void    返回类型 
	* @throws 
	* @author zhouxin   
	* @date 2015年6月1日 下午2:54:34
	 */
	@SuppressWarnings("unchecked")
	public void saveImpOrd(BusinessRequest res) throws IOException {
		OrdDto dto = super.getDto(OrdDto.class, res);
		dto.setOpFlg("0");
		try{
			OrderDetail orderDetail;
			if(dto.getOrderItmId()==null){
				orderDetail=new OrderDetail();
				orderDetail.setOrderUserId(Long.valueOf(super.getLoginInfo().get("USERID").toString()));
				orderDetail.setOrderHopId(Long.valueOf(super.getLoginInfo().get("HOSPID").toString()));
				orderDetail.setOrderRecLoc(Long.valueOf(super.getLoginInfo().get("LOCID").toString()));
				orderDetail.setOrderDate(new Date());
				orderDetail.setOrderImpId(dto.getImpId());
				VenInc venInc=commonService.get(VenInc.class, dto.getVenIncId());;
				
				DetachedCriteria criteria = DetachedCriteria.forClass(OrderDetail.class);
				criteria.setProjection(Property.forName("orderNo"));
				criteria.add(Restrictions.eq("orderVenId", venInc.getVenIncVenid()));
				criteria.add(Restrictions.eq("orderImpId", dto.getImpId()));
				List<String> orderDetails=commonService.findByDetachedCriteria(criteria);
				if(orderDetails.size()>0){
					orderDetail.setOrderNo(orderDetails.get(0));
				}else{
					orderDetail.setOrderNo(nurseBlh.GetAppNo("ORD"));
				}
			}else{
				orderDetail=commonService.get(OrderDetail.class, dto.getOrderItmId());
			}	
		
		if(orderDetail.getOrderState()==null){
			orderDetail.setOrderRp(dto.getRp());
			float fac=0f;
					
			if(dto.getVenIncId()!=null){
				orderDetail.setOrderVenIncId(dto.getVenIncId());
				orderDetail.setOrderHopIncId(dto.getIncId());
				VenInc venInc=commonService.get(VenInc.class, dto.getVenIncId());
				HopInc hopInc=commonService.get(HopInc.class, dto.getIncId());
				orderDetail.setOrderHopUom(hopInc.getIncUomname());
				orderDetail.setOrderVenId(venInc.getVenIncVenid());
				orderDetail.setOrderVenuom(venInc.getVenIncUomname());
				String[] propertyNames={"hopIncId","venIncId"};
				Object[] values={dto.getIncId(),dto.getVenIncId()};
				List<VenHopInc> venHopIncs=commonService.findByProperties(VenHopInc.class, propertyNames, values);
				if(venHopIncs.size()>0){
					fac=venHopIncs.get(0).getVenFac().floatValue()/venHopIncs.get(0).getHopFac().floatValue();
					orderDetail.setOrderFac(fac);
				}
				orderDetail.setOrderRp(venInc.getVenIncPrice().floatValue()/fac);
			}
			orderDetail.setOrderHopQty(dto.getQty());
			if(dto.getQty()!=null){
				orderDetail.setOrderVenQty(dto.getQty().floatValue()/orderDetail.getOrderFac().floatValue());
			}else{
				orderDetail.setOrderVenQty(0f);
			}
			
			commonService.saveOrUpdate(orderDetail);
			dto.setOpFlg("1");
			dto.setMsg(orderDetail.getOrderId().toString());
		}else{
			dto.setMsg("订单已提交，不能修改");
		}
		
		}catch(Exception e){
			e.printStackTrace();
			dto.setMsg(e.getMessage());
		}finally{
			WebContextHolder.getContext().getResponse().getWriter().write(JsonUtils.toJson(dto));
		}
	}
	
	
	/**
	 * 
	* @Title: saveImpOrd 
	* @Description: TODO(保存上传订单明细) 
	* @param @param res
	* @param @throws IOException    设定文件 
	* @return void    返回类型 
	* @throws 
	* @author zhouxin   
	* @date 2015年5月29日 下午5:35:46
	 */
	@SuppressWarnings({ "unchecked" })
	public void cmpImpOrd(BusinessRequest res) throws IOException {
		OrdDto dto = super.getDto(OrdDto.class, res);
		dto.setOpFlg("1");
		try{
			DetachedCriteria criteriaOrd = DetachedCriteria.forClass(Ord.class);
			criteriaOrd.add(Restrictions.eq("ordId",dto.getImpId()));
			List<Ord> ords=commonService.findByDetachedCriteria(criteriaOrd);
			if(ords.size()==0){
				dto.setOpFlg("-1");
				dto.setMsg("入参错误");
				return;
			}
			if(org.apache.commons.lang.StringUtils.isNotBlank(ords.get(0).getOrdCmpFlag())){
				if(ords.get(0).getOrdCmpFlag().equals("1")){
					dto.setOpFlg("-1");
					dto.setMsg("订单已确认完成");
					return;
				}
			}
			Ord ord=ords.get(0);
			ord.setOrdCmpFlag("1");
			commonService.saveOrUpdate(ord);
			DetachedCriteria criteriaOrderNo = DetachedCriteria.forClass(OrderDetail.class);
			criteriaOrderNo.add(Restrictions.eq("orderImpId",dto.getImpId()));
			List<OrderDetail> orders=commonService.findByDetachedCriteria(criteriaOrderNo);	
			for(OrderDetail orderDetail:orders){
				if(orderDetail.getOrderState()==null){
					orderDetail.setOrderState(1l);
					commonService.saveOrUpdate(orderDetail);
				}
			}
		}catch(Exception e){
			dto.setOpFlg("-1");
			e.printStackTrace();
			dto.setMsg(e.getMessage());
		}finally{
			WebContextHolder.getContext().getResponse().getWriter().write(JsonUtils.toJson(dto));
		}
	}
	/**
	 * 
	* @Title: deleteImpOrd 
	* @Description: TODO(删除上传订单明细) 
	* @param @param res
	* @param @throws IOException    设定文件 
	* @return void    返回类型 
	* @throws 
	* @author zhouxin   
	* @date 2015年5月29日 下午5:35:20
	 */
	public void deleteImpOrd(BusinessRequest res) throws IOException {
		
		OrdDto dto = super.getDto(OrdDto.class, res);
		dto.setOpFlg("0");
		try{
		OrderDetail orderDetail=commonService.get(OrderDetail.class, dto.getOrderItmId());
		if(orderDetail.getOrderState()==null){
			commonService.delete(orderDetail);
			dto.setOpFlg("1");
		}else{
			dto.setMsg("订单已提交，不能删除");
		}
		
		}catch(Exception e){
			e.printStackTrace();
			dto.setMsg(e.getMessage());
		}finally{
			WebContextHolder.getContext().getResponse().getWriter().write(JsonUtils.toJson(dto));
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void upload(BusinessRequest res) throws IOException{
		OrdDto dto = super.getDto(OrdDto.class, res);
		
		Long hopID=null;
		Long userID=null;
		Long locId=null;
		try{
			hopID=WebContextHolder.getContext().getVisit().getUserInfo().getHopId();
			userID=Long.valueOf(WebContextHolder.getContext().getVisit().getUserInfo().getId());
			locId=WebContextHolder.getContext().getVisit().getUserInfo().getLocId();
		}catch(Exception e){
			dto.setOpFlg("-11");
			dto.setMsg("<br>登录超时,请重新登录:");
			WebContextHolder.getContext().getResponse().getWriter().write(JsonUtils.toJson(dto));
			return;
		}
		dto.setMsg("<BR>");
		//生成随机文件名
		String newFileName =UUID.randomUUID().toString();
		//获取文件存储路径
		String storageFileName = ServletActionContext.getServletContext().getRealPath("/uploadtmps");
		//判断文件存储路径是否存在，若不存在则自动新建
		File document = new File(storageFileName);
		if (!document.exists()) {
			document.mkdir();
		}

		File dstFile = new File(storageFileName,newFileName); 
        com.dhcc.framework.util.FileUtils.copyFile(dto.getUpload(), dstFile,BaseConstants.BUFFER_SIZE);
        
        //
        Map<Integer,String> modelMap=new HashMap<Integer,String>();
        SysImpModelDto SysImpModelDto=new SysImpModelDto();
        SysImpModelDto.setImpModel(new ImpModel());
        SysImpModelDto.getImpModel().setType("ORDER");
        try {
        	List<ImpModel> listImpModels=sysImpModelService.getModelList(SysImpModelDto);
        	for(int i=0;i<listImpModels.size();i++){
            	modelMap.put(Integer.valueOf(listImpModels.get(i).getSeq().toString()), listImpModels.get(i).getName());
            }
        }catch (Exception e) {
        	dto.setOpFlg("-11");
			dto.setMsg("<br>Exception:"+e.getMessage());
			WebContextHolder.getContext().getResponse().getWriter().write(JsonUtils.toJson(dto));
			return;
		}
        
        Map<String, List<OrderDetail>> venMap = new HashMap<String, List<OrderDetail>>();
        
        SysLog log=new SysLog();
        log.setOpName("医院订单excel导入");
		log.setOpIp(WebContextHolder.getContext().getRequest().getRemoteAddr().toString());
		log.setOpDate(new Date());
		log.setOpResult(dto.getMsg());
		log.setOpType("excel上传");
		log.setOpUser(commonService.get(NormalAccount.class, userID).getAccountAlias());

        
        //读取excel
        try {

        	//读取Excel文件
        	Workbook workbook = null;
			Sheet sheet = null;
			Row row = null;
			Cell cell = null;
			String prefix=dto.getUploadFileName().substring(dto.getUploadFileName().lastIndexOf(".")+1);
			if(prefix.equals("xls")){
				workbook = new HSSFWorkbook(new FileInputStream(storageFileName + File.separator + newFileName));
			}else if(prefix.equals("xlsx"))  {
				workbook = new XSSFWorkbook(new FileInputStream(storageFileName + File.separator + newFileName));
			}else{
				dto.setOpFlg("-11");
				dto.setMsg("<br>文件类型错误:");
				WebContextHolder.getContext().getResponse().getWriter().write(JsonUtils.toJson(dto));
				return;
			}
			
			
			
			sheet = workbook.getSheetAt(0);
			dto.setOpFlg("1");
			
			for (int numRows = 1; numRows <= sheet.getLastRowNum(); numRows++) {
				
				row = sheet.getRow(numRows);
				String orderNo="";
				//String purLoc="";
				String emflag="";
				String destion="";
				Date requireDate=null;
				String venCode="";
				float rp=0f;
				float qty=0f;
				String incCode="";
				Long vendorId=null;
				Long destId=null;
				Long incId=null;
				for (int numCells = 0; numCells <= row.getLastCellNum(); numCells++) {
					cell = row.getCell(numCells);
					String colNameString=modelMap.get(numCells);
					if(StringUtils.isNullOrEmpty(colNameString)) {colNameString=" ";};

					switch (colNameString) {
							case "订单号":
								if(cell!=null){
									cell.setCellType(Cell.CELL_TYPE_STRING);
									orderNo=cell.getStringCellValue();
								}
								break;
//							case "入库科室":
//								if(cell!=null){
//									cell.setCellType(HSSFCell.CELL_TYPE_STRING);
//									purLoc=cell.getStringCellValue();	
//								}
//								break;	
							case "是否加急":
								if(cell!=null){
									cell.setCellType(Cell.CELL_TYPE_STRING);
									emflag=cell.getStringCellValue();
								}
								break;
							case "收货地址":
								if(cell!=null){
									cell.setCellType(Cell.CELL_TYPE_STRING);
									destion=cell.getStringCellValue();
								}
								break;
							case "要求送货时间":
								if(cell!=null){
									requireDate=cell.getDateCellValue();
								}
								break;
							case "供应商代码":
								if(cell!=null){
									cell.setCellType(Cell.CELL_TYPE_STRING);
									venCode=cell.getStringCellValue();
								}
								break;
							case "HIS药品代码":
								if(cell!=null){
									cell.setCellType(Cell.CELL_TYPE_STRING);
									incCode=cell.getStringCellValue();
								}
								break;	
							case "数量":
								if(cell!=null){
									cell.setCellType(Cell.CELL_TYPE_NUMERIC);
									qty=(float)cell.getNumericCellValue();
								}
								break;	
							case "进价":
								if(cell!=null){
									cell.setCellType(Cell.CELL_TYPE_NUMERIC);
									rp=(float)cell.getNumericCellValue();
								}
								break;		
						}
				}
				
				if(StringUtils.isNullOrEmpty(orderNo)){
					dto.setOpFlg("-1");
					dto.setMsg(dto.getMsg()+"<br>第"+numRows+"行订单号不能为空");
				}else{
					DetachedCriteria criteriaOrderNo = DetachedCriteria.forClass(OrderDetail.class);
					criteriaOrderNo.add(Restrictions.eq("orderHisNo",orderNo));
					criteriaOrderNo.add(Restrictions.eq("orderHopId", hopID));
					List<OrderDetail> orders=commonService.findByDetachedCriteria(criteriaOrderNo);
					if(orders.size()>0){
						dto.setOpFlg("-1");
						dto.setMsg(dto.getMsg()+"<br>第"+numRows+"行订单号"+orderNo+"已经上传");
					}
				}
				
				
				//供应商
				if(StringUtils.isNullOrEmpty(venCode)){
					dto.setOpFlg("-1");
					dto.setMsg(dto.getMsg()+"<br>第"+numRows+"行供应商代码不能为空");
				}else{
					HopVendor hopVendor=hopVendorService.findVendorByCode(venCode,hopID);
					if(hopVendor==null){
						dto.setOpFlg("-1");
						dto.setMsg(dto.getMsg()+"<br>第"+numRows+"行:"+venCode+"，供应商代码错误");
						continue;
					}
					if(hopVendor.getHopVenId()==null){
						dto.setOpFlg("-1");
						dto.setMsg(dto.getMsg()+"<br>第"+numRows+"行:"+venCode+"，供应商代码没有对照");
						continue;
					}else{
						vendorId=hopVendor.getHopVenId();
					}
				}
				
				//收货地址
				if(StringUtils.isNullOrEmpty(destion)){
					dto.setOpFlg("-1");
					dto.setMsg(dto.getMsg()+"<br>第"+numRows+"行收货地址不能为空");
				}else{
					HopCtlocDestination ctlocDestination=hopCtlocDestinationService.getDesctionByCode(destion, hopID);
					if(ctlocDestination==null){
						dto.setOpFlg("-1");
						dto.setMsg(dto.getMsg()+"<br>第"+numRows+"行:"+destion+"，收货地址错误");
						continue;
					}else{
						destId=ctlocDestination.getHopCtlocDestinationId();
					}
				}

				if(StringUtils.isNullOrEmpty(incCode)){
					dto.setOpFlg("-1");
					dto.setMsg(dto.getMsg()+"<br>第"+numRows+"行供his药品代码不能为空");
				}else{
					if(incCode.contains("'")){
						incCode=incCode.substring(incCode.indexOf("'")+1);
					}
					
					incId=hopIncService.getHopIncByCode(incCode,hopID);
					if(incId==null){
						dto.setOpFlg("-1");
						dto.setMsg(dto.getMsg()+"<br>第"+numRows+"行"+incCode+"，医院药品代码错误");
						continue;
					}
				}
				
				if(qty<=0f){
					dto.setOpFlg("-1");
					dto.setMsg(dto.getMsg()+"<br>第"+numRows+"行供订单数量不能为0");
				}
				
				//医院对照的第一条供应商商品
				String[] VenHopIncproNames={"hopIncId","venHopAuditflag"};
				Object[] VenHopIncproValues={incId,"Y"};
				List<VenHopInc> venHopIncs = commonService.findByProperties(VenHopInc.class, VenHopIncproNames, VenHopIncproValues);
				if (venHopIncs.size() == 0) {
					dto.setOpFlg("-1");
					dto.setMsg(dto.getMsg()+"<br>第"+numRows+"行"+incCode+"，医院药品没有做对照");
					continue;
				}
				VenInc VenInc=null;
				VenHopInc VenHopInc=null;
				for (VenHopInc venHopInc : venHopIncs) {
					VenInc venInc = commonService.get(VenInc.class, venHopInc.getVenIncId());
					if (venInc.getVenIncVenid().toString().equals(vendorId.toString())) {
						VenHopInc=venHopInc;
						VenInc=venInc;
					}
				}
				if(VenInc==null){
					dto.setOpFlg("-1");
					dto.setMsg(dto.getMsg()+"<br>第"+numRows+"行"+incCode+"，医院药品没有做对照2");
					continue;
				}

				OrderDetail orderDetail=new OrderDetail();

				HopInc hopInc=commonService.get(HopInc.class, incId);
				orderDetail.setOrderVenId(vendorId);
				orderDetail.setOrderHopId(hopID);
				orderDetail.setOrderDate(new Date());
				orderDetail.setOrderHopIncId(hopInc.getIncId());
				orderDetail.setOrderRecLoc(locId);
				orderDetail.setOrderUserId(userID);
				orderDetail.setOrderHopUom(hopInc.getIncUomname());
				orderDetail.setOrderVenuom(VenInc.getVenIncUomname());
				orderDetail.setOrderVenIncId(VenInc.getVenIncId());
				orderDetail.setOrderRp(rp); // 供应商单位进价
				//orderDetail.setOrderState(1l);
				float fac =VenHopInc.getVenFac().floatValue() / VenHopInc.getHopFac().floatValue();
				float hopQty = qty;
				float venQty = qty/fac;
				orderDetail.setOrderHopQty(hopQty);
				orderDetail.setOrderVenQty(venQty);
				orderDetail.setOrderFac(fac);
				orderDetail.setOrderRecDestination(destId);
				orderDetail.setOrderEmFlag(emflag);
				orderDetail.setOrderHisNo(orderNo);
				
				if(venMap.containsKey(vendorId.toString())){
					venMap.get(vendorId.toString()).add(orderDetail);
				}else{
					List<OrderDetail> orderDetails=new ArrayList<OrderDetail>();
					orderDetails.add(orderDetail);
					venMap.put(vendorId.toString(),orderDetails);
				}
			}		
			if(!dto.getOpFlg().equals("1")){
				WebContextHolder.getContext().getResponse().getWriter().write(JsonUtils.toJson(dto));
				return;
			}
			ordService.impOrd(venMap);
			dto.setMsg("操作成功");
			
			for(Map.Entry<String, List<OrderDetail>> entry: venMap.entrySet()) {
				   log.setOpArg(log.getOpArg()+"."+ JsonUtils.toJson(entry.getValue()));
			}	   
			WebContextHolder.getContext().getResponse().getWriter().write(JsonUtils.toJson(dto));

        } catch (Exception e) {
			e.printStackTrace();
			dto.setOpFlg("-11");
			dto.setMsg(dto.getMsg()+"<br>程序异常:"+e.getLocalizedMessage());
			log.setOpResult("falie:exception:"+e.getMessage()+dto.getMsg());
		
			WebContextHolder.getContext().getResponse().getWriter().write(JsonUtils.toJson(dto));
		}finally{
			commonService.saveOrUpdate(log);
			FileUtils.forceDelete(dstFile);
		}
	}
	
	public void listDelVItmNew(BusinessRequest res) throws IOException {
		
		OrdDto dto = super.getDto(OrdDto.class, res);
		
		//调用对应的service方法
		ordService.listDeliverItm(dto);
		WebContextHolder.getContext().getResponse().getWriter().write(JsonUtils.toJson(dto.getDelvVos()));
	}
	
	
	/**
	 * 
	* @Title: saveOrdImp 
	* @Description: TODO(手工新建订单插入导入主表) 
	* @param @param res
	* @param @throws IOException    设定文件 
	* @return void    返回类型 
	* @throws 
	* @author zhouxin   
	* @date 2015年6月9日 上午11:01:22
	 */
	public void saveOrdImp(BusinessRequest res) throws IOException {
		
		OrdDto dto = super.getDto(OrdDto.class, res);
		dto.setOperateResult(new OperateResult());
		try {
			Ord ord=new Ord();
			ord.setOrdDate(new Date());
			ord.setOrdHopId(Long.valueOf(super.getLoginInfo().get("HOSPID").toString()));
			ord.setOrdLocId(Long.valueOf(super.getLoginInfo().get("LOCID").toString()));
			ord.setOrdNo(nurseBlh.GetAppNo("DORD"));
			commonService.saveOrUpdate(ord);
			dto.getOperateResult().setResultCode("1");
			dto.getOperateResult().setResultContent(ord.getOrdId());
		} catch (Exception e) {
			e.printStackTrace();
			dto.getOperateResult().setResultCode("-1");
			dto.getOperateResult().setResultContent(e.getMessage());
		}finally{
			super.writeJSON(dto.getOperateResult());
		}
	}
}
