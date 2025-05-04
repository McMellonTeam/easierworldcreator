package fr.rodofire.ewc.toml;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static fr.rodofire.ewc.toml.ValueReaders.VALUE_READERS;

class ArrayValueReader implements ValueReader {

  static final ArrayValueReader ARRAY_VALUE_READER = new ArrayValueReader();

  @Override
  public boolean canRead(String s) {
    return s.startsWith("[");
  }

  @Override
  public Object read(String s, AtomicInteger index, Context context) {
    AtomicInteger line = context.line;
    int startLine = line.get();
    int startIndex = index.get();
    List<Object> arrayItems = new ArrayList<>();
    boolean terminated = false;
    boolean inComment = false;
    Results.Errors errors = new Results.Errors();
    
    for (int i = index.incrementAndGet(); i < s.length(); i = index.incrementAndGet()) {

      char c = s.charAt(i);
      
      if (c == '#' && !inComment) {
        inComment = true;
      } else if (c == '\n') {
        inComment = false;
        line.incrementAndGet();
      } else if (inComment || Character.isWhitespace(c) || c == ',') {
        continue;
      } else if (c == '[') {
        Object converted = read(s, index, context);
        if (converted instanceof Results.Errors) {
          errors.add((Results.Errors) converted);
        } else if (!isHomogenousArray(converted, arrayItems)) {
          errors.heterogenous(context.identifier.getName(), line.get());
        } else {
          arrayItems.add(converted);
        }
      } else if (c == ']') {
        terminated = true;
        break;
      } else {
        Object converted = VALUE_READERS.convert(s, index, context);
        if (converted instanceof Results.Errors) {
          errors.add((Results.Errors) converted);
        } else if (!isHomogenousArray(converted, arrayItems)) {
          errors.heterogenous(context.identifier.getName(), line.get());
        } else {
          arrayItems.add(converted);
        }
      }
    }
    
    if (!terminated) {
      errors.unterminated(context.identifier.getName(), s.substring(startIndex, s.length()), startLine);
    }
    
    if (errors.hasErrors()) {
      return errors;
    }
    
    return arrayItems;
  }

  private boolean isHomogenousArray(Object o, List<?> values) {
    return values.isEmpty() || values.getFirst().getClass().isAssignableFrom(o.getClass()) || o.getClass().isAssignableFrom(values.getFirst().getClass());
  }
  
  private ArrayValueReader() {}
}
