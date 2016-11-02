 - 下个版本重写异步任务串行化队列，参考RxAndroid

 1.1.0
 - 支持广播解析 done
 - 去掉SERVICE依赖 done
 - 连接支持可配置，重试次数，超时，可取消任务
 - 代码优化，proxy优化一下，名称精简 done
 - 写一个完整的demo  done
 - 当调用refreshCache后，先记下来，下次连接的时候会refreshCache done
 - connect回调别用Bundle  done
 - 支持write no resp done

 - 完善连接状态监听 done
 - 可打开和关闭蓝牙 done

 1.2.0
 - 异步任务队列单独抽象出来，可参考RxAndroid
 - 支持新版Android提供的蓝牙接口
 - Hook掉系统的BluetoothGatt
 - discoverService重试和超时
 - 研究可否手机主动发起更改连接参数
