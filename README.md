# UML2Choco
Compiler from EMF.xmi and OCL.atl to CSP.choco

## API:
- Load(xmi)
- Load(xmi,atl)
- Solve()
- Save(xmi)

## 2 Compliers:
- XMI2Choco : which makes a Choco Model from an XMI file 
- OCL2Choco : which adds to the Choco Model from an OCL file

## Var Compiler Annotation
You can annotate different models (.xcore, .xmi, .atl) to guide compilation
- choose between optimisation, fixing, exploration, etc..
- no annotations does model validation and simple completion
- limit the scope of the solver on large models


## Example Models
meta-model in xcore
```xcore
class Object {
  attrib : int
  ref : reference[0-5] Object
}
```
constraint meta-model in ocl
```
context Object : SubSetSum(target : int) =
  let query = self.var(ref).ref.ref.attribute in
    query.sum() = target and
    query.isUnique();
```
model to solve for
```xmi
<object 1>
  <attribute> 10 </attribute>
</object>
<object 2>
  <attribute> 11 </attribute>
</object>
<object 3>
  <attribute> 12 </attribute>
</object>
<object 4>
  <attribute> 13 </attribute>
</ object>
```
from this we make a constraint model to solve
