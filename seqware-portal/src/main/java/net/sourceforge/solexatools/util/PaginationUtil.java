package net.sourceforge.solexatools.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Study;

import org.springframework.context.support.MessageSourceAccessor;

public class PaginationUtil {

  public final static int SIZE_COUNT_TREE = 20;
  public final static int SIZE_COUNT_FILE = 5;

  private static String getOperation(HttpServletRequest request) {
    String operation = (String) request.getParameter("action");
    if (operation == null) {
      operation = "";
    }
    return operation;
  }

  private static <T extends Object> List<T> subListImpl(HttpServletRequest request, String namePage, int sizeCount,
      List<T> list) {
    List<T> result = Collections.synchronizedList(new LinkedList<T>());
    if (list.isEmpty())
      return result;

    HttpSession session = request.getSession(false);
    Integer numberPage = (Integer) session.getAttribute(namePage);
    if (numberPage == null)
      numberPage = 0;

    int count = numberPage;

    String operation = getOperation(request);
    // System.out.println("Operation=" + operation);
    int size = list.size();
    int maxCount = size / sizeCount;

    if (size % sizeCount == 0) {
      maxCount--;
    }
    // check count
    if (count > maxCount)
      count = maxCount;

    // System.out.println("maxCount = " + maxCount + ";");

    if (operation.equals("next")) {

      if (count < maxCount) {
        count++;
      }
    }
    if (operation.equals("previous")) {
      if (count > 0) {
        count--;
      }
    }
    if (operation.equals("first")) {
      count = 0;
    }
    if (operation.equals("last")) {
      count = maxCount;
    }

    int addCount = sizeCount;
    if (count == maxCount) {
      if (size % sizeCount != 0) {
        addCount = size % sizeCount;
      }
    }

    // System.out.println("Count = " + count + "; addCount = " + addCount);

    int fromIndex = count * sizeCount;
    int toIndex = fromIndex + addCount;

    // System.out.println("From = " + fromIndex + "; To = " + toIndex);

    result = list.subList(fromIndex, toIndex);

    session.setAttribute(namePage, count);

    // removeOperation(request);
    return result;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static List subList(int page, int sizeCount, List list) {
    List<File> result = Collections.synchronizedList(new LinkedList<File>());
    if (list.isEmpty())
      return result;

    int size = list.size();
    int maxCount = size / sizeCount;

    if (size % sizeCount == 0) {
      maxCount--;
    }

    int addCount = sizeCount;
    if (page == maxCount) {
      if (size % sizeCount != 0) {
        addCount = size % sizeCount;
      }
    }

    int fromIndex = page * sizeCount;
    int toIndex = fromIndex + addCount;

    result = list.subList(fromIndex, toIndex);

    return result;
  }

  public static <T extends Object> List<T> subList(HttpServletRequest request, String namePage, List<T> list) {
    return subListImpl(request, namePage, SIZE_COUNT_TREE, list);
  }

  public static <T extends Object> List<T> subList(HttpServletRequest request, String namePage, int sizeCount,
      List<T> list) {
    return subListImpl(request, namePage, sizeCount, list);
  }

  public static List subListSD(HttpServletRequest request, String namePage, List list) {
    return subListImpl(request, namePage, SIZE_COUNT_FILE, list);
  }

  public static PageInfo getPageInfo(HttpServletRequest request, String namePage, List listView, List listAll,
      String nameNode, String nameNodes, MessageSourceAccessor ma) {
    PageInfo pageInfo = getPageInfoImpl(request, namePage, listView.size(), listAll.size(), SIZE_COUNT_TREE, ma);
    String str = "";

    // get node name
    if (listView.size() > 1) {
      str = " " + ma.getMessage(nameNodes) + ",";
    } else if (listView.size() == 1) {
      str = " " + ma.getMessage(nameNode) + ",";
    }

    pageInfo.setInfo(pageInfo.getInfo() + str);

    return pageInfo;
  }

  public static PageInfo getPageInfoSD(HttpServletRequest request, String namePage, List listView, List allList,
      MessageSourceAccessor ma) {
    return getPageInfoImpl(request, namePage, listView.size(), allList.size(), SIZE_COUNT_FILE, ma);
  }

  private static PageInfo getPageInfoImpl(HttpServletRequest request, String namePage, int countView, int countAll,
      int countPage, MessageSourceAccessor ma) {

    PageInfo pageInfo = new PageInfo();
    String info = "";
    if (countView == 0) {
      pageInfo.setIsStart(true);
      pageInfo.setIsEnd(true);
      pageInfo.setInfo(info);
      return pageInfo;
    }

    Integer numberPage = (Integer) request.getSession(false).getAttribute(namePage);
    if (numberPage == null)
      numberPage = 0;

    Integer startPos = (numberPage) * countPage + 1;
    Integer endPos = (numberPage) * countPage + countView;

    if (countView == 1) {
      info = startPos.toString();
      if (info.endsWith("1")) {
        info = info + ma.getMessage("pagination.st");
      } else {
        info = info + ma.getMessage("pagination.th");
      }
    } else {
      info = startPos + " &mdash; " + endPos;
    }

    info = info + " " + ma.getMessage("pagination.of") + " " + countAll;

    if (startPos == 1) {
      pageInfo.setIsStart(true);
    }
    if (endPos == countAll) {
      pageInfo.setIsEnd(true);
    }
    pageInfo.setInfo(info);
    return pageInfo;
  }

  public static int getPageNumber(HttpServletRequest request, Study requiredStudy, List<Study> allList) {
    int size = allList.size();
    int studyIndex = 1;
    for (int i = 0; i < size; i++) {
      if (requiredStudy.equals(allList.get(i))) {
        studyIndex = i + 1;
        break;
      }
    }

    int pageNumber = studyIndex / SIZE_COUNT_TREE;
    if (studyIndex % SIZE_COUNT_TREE > 0) {
      pageNumber++;
    }
    return --pageNumber;
  }
}
